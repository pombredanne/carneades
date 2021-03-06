;;; Copyright (c) 2012 Fraunhofer Gesellschaft
;;; Licensed under the EUPL V.1.1

(ns carneades.analysis.web.icanhaz.core
  (:use [jayq.util :only [log]])
  (:require [clojure.string :as str])
  (:refer-clojure :exclude [get]))

(defn get
  "Returns the ICanHaz HTML template"
  [template-key variables]
  (let [tname (str/replace (clj->js template-key) "-" "_")]
    (.call (aget js/ich tname) js/ich (clj->js variables))))
