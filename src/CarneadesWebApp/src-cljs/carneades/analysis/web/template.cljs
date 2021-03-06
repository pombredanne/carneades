;;; Copyright (c) 2012 Fraunhofer Gesellschaft
;;; Licensed under the EUPL V.1.1

;; TODO this should be moved the views directory
(ns carneades.analysis.web.template
  (:use [jayq.util :only [log]])
  (:require [carneades.analysis.web.icanhaz.core :as ich])
  (:refer-clojure :exclude [get]))

(defn get
  "Returns an ICanHaz template,
variables from the Messages.properties file are automatically passed to the template constructor"
  [template variables]
  (let [entries (keys (js->clj js/jQuery.i18n.map))
        texts (apply hash-map (mapcat (fn [e] [(keyword e) (js/jQuery.i18n.prop e)]) entries))
        augmented (merge texts variables)]
    
    ;; (log "variables =")
    ;; (log (clj->js texts))
    
    (ich/get template augmented)))

