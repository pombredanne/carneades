;;; Carneades Argumentation Library and Tools.
;;; Copyright (C) 2010 Thomas F. Gordon, Fraunhofer FOKUS, Berlin
;;; 
;;; This program is free software: you can redistribute it and/or modify
;;; it under the terms of the GNU Lesser General Public License version 3 (LGPL-3)
;;; as published by the Free Software Foundation.
;;; 
;;; This program is distributed in the hope that it will be useful, but
;;; WITHOUT ANY WARRANTY; without even the implied warranty of
;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
;;; General Public License for details.
;;; 
;;; You should have received a copy of the GNU Lesser General Public License
;;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns carneades.engine.proofstandard
  (:use clojure.contrib.def))

;; dispatch on the proof standard
(defmulti satisfies? (fn [_ ps _ _ _] ps))

;; scintilla?
(defmethod satisfies? :se [ag ps pro-args con-args all-premises-hold?]
  "argument-graph (seq-of argument) (seq-of argument) -> boolean"
  (not (nil? (some #(all-premises-hold? ag %) pro-args))))

;; dialectically-valid?
(defmethod satisfies? :dv [ag ps pro-args con-args all-premises-hold?]
  "argument-graph (seq-of argument) (seq-of argument) -> boolean"
  (and (satisfies? ag :se pro-args con-args all-premises-hold?)
       (not-any? #(all-premises-hold? ag %) con-args)))

(defn- best-arg [ags]
  (if (empty? ags) 0.0 (apply max (map :weight ags))))

;; best-argument?
(defmethod satisfies? :ba [ag ps pro-args con-args all-premises-hold?]
  "argument-graph (seq-of argument) (seq-of argument) -> boolean"
  (let [pro (filter #(all-premises-hold? ag %) pro-args)
        con (filter #(all-premises-hold? ag %) con-args)
        best-pro (best-arg pro)
        best-con (best-arg con)]
    (> best-pro best-con)))

;; clear-and-convincing-evidence?
(defmethod satisfies? :cce [ag ps pro-args con-args all-premises-hold?]
  "argument-graph (seq-of argument) (seq-of argument) -> boolean"
  (let [pro (filter #(all-premises-hold? ag %) pro-args)
        con (filter #(all-premises-hold? ag %) con-args)
        best-pro (best-arg pro all-premises-hold?)
        best-con (best-arg con all-premises-hold?)
        alpha 0.5
        beta 0.3]
    (and (> best-pro best-con) ; i.e. preponderance of the evidence test is met
         (> best-pro alpha)
         (> (- best-pro best-con) beta))))

;; beyond-reasonable-doubt?
(defmethod satisfies? :brd [ag ps pro-args con-args all-premises-hold?]
  "argument-graph (list-of argument) (list-of argument) -> boolean"
  (let [pro (filter #(all-premises-hold? ag %) pro-args)
        con (filter #(all-premises-hold? ag %) con-args)
        best-pro (best-arg pro all-premises-hold?)
        best-con (best-arg con all-premises-hold?)
        alpha 0.5
        beta 0.5
        gamma 0.2]
    (and
     ; clear and convincing evidence test is also met
     (> best-pro best-con)
     (> best-pro alpha)
     (> (- best-pro best-con) beta)
     (< best-con gamma))))