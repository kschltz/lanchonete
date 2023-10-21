(ns mba-fiap.adapter.http.server
  (:require [integrant.core :as ig]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.interceptor.helpers :as interceptor]
            [mba-fiap.adapter.cliente-rest :as cliente-rest]
            [clojure.data.json :as json]))


(defn context-interceptor [context]
  (interceptor/on-request #(assoc % :app-context context)))
(def response-json-body
  (interceptor/on-response #(update % :body json/write-str)))


(defn routes []
  (route/expand-routes
    (into []
          [(cliente-rest/cliente-routes)])))

(defn server [{:keys [env port join? app-context]}]
  (let [ctx-interceptor (context-interceptor app-context)]
    (cond-> {:env env
             ::http/routes (routes)
             ::http/resource-path "/public"
             ::http/type :jetty
             ::http/join? join?
             ::http/port port}
            :always http/default-interceptors
            :always (update :io.pedestal.http/interceptors
                            #(vec
                               (concat %
                                       [ctx-interceptor
                                        response-json-body
                                        ])))
            (or (= :dev env)
                (= :test env)) http/dev-interceptors
            :then http/create-server)))


(defmethod ig/init-key ::server [_ cfg]
  (http/start (server cfg)))

(defmethod ig/halt-key! ::server [_ server]
  (http/stop server))