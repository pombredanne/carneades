;;; Copyright (c) 2012 Fraunhofer Gesellschaft
;;; Licensed under the EUPL V.1.1

(ns carneades.policy-analysis.web.views.pages
  (:use [hiccup core page-helpers])
  (:require [net.cgrand.enlive-html :as html]
            [carneades.config.config :as config]))

(html/deftemplate index "carneades/public/index.html" [])

(defn render [t]
      (apply str t))

(defn index-page []
  (render (index)))

(defn config-page []
  (html5
   [:body
    (str "<hr>configfilename =<br><br> " config/configfilename "<br><br>"
         "<hr>properties = <br>"
         (apply str (sort (map (fn [[k v]] (str "<br> " k "=" v)) config/properties)))
         "<hr><br>java properties = <br>"
         (apply str (sort (map (fn [[k v]] (str "<br><br> " k "=" v)) (System/getProperties)))))]))
