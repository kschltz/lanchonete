(ns mba-fiap.adapter.http.server
  (:require [integrant.core :as ig]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [mba-fiap.adapter.cliente-rest :as cliente-rest]))


(defn routes []
  (route/expand-routes
    (cliente-rest/cliente-routes)))

(defn server [{:keys [env port join?]}]
  (cond-> {:env env
           ::http/routes (routes)
           ::http/resource-path "/public"
           ::http/type :jetty
           ::http/join? join?
           ::http/port port}
          :always http/default-interceptors
          (or (= :dev env)
              (= :test env)) http/dev-interceptors
          :then http/create-server))

(defmethod ig/init-key ::server [_ cfg]
  (http/start (server cfg)))

(defmethod ig/halt-key! ::server [_ server]
  (http/stop server))