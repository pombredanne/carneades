(ns carneades.web.application.routes
  (:use compojure.core
        ring.adapter.jetty ;; <- to comment when building WAR
        ring.middleware.params
        ring.middleware.session
        carneades.web.application.views.pages
        carneades.web.service)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]))

(def carneades-application-routes
     [(GET "/" [] (index-page))
      (route/resources "/")
      ;; (route/not-found "Page not found")
      ])

(def allroutes (concat carneades-application-routes carneades-web-service-routes))
(def app (handler/site (apply routes allroutes)))

;; to comment when building the WAR:
(defonce server
  (run-jetty #'app {:join? false :port 8080}))

;; (defonce carneades-service-server
;;   (run-jetty #'carneades-web-service {:join? false :port 9000}))