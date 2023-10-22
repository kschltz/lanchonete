(ns mba-fiap.adapter.pedido-rest
  (:require [mba-fiap.service.pedido :as pedido.service]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.ring-middlewares :as middlewares]))


(defn cadastrar-pedido [request]
  (let [repository (get-in request [:app-context :repository/pedido])
        data (:json-params request)
        result (pedido.service/checkout repository data)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body result}))

(defn pedido-routes []
  [["/pedido" ^:interceptors [(body-params/body-params)
                               middlewares/params
                               middlewares/keyword-params]
    {:post `cadastrar-pedido}]])

