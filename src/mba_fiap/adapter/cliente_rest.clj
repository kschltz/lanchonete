(ns mba-fiap.adapter.cliente-rest
  (:require [mba-fiap.service.cliente :as cliente.service]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.ring-middlewares :as middlewares]))


(defn cadastrar-cliente [request]
  (let [_ (tap> request)
        repository (get-in request [:app-context :repository/cliente])
        data (:json-params request)
        result (cliente.service/cadastrar-cliente repository data)]
    {:status 200
     :body result}))

(defn cliente-routes []
  ["/cliente" ^:interceptors [(body-params/body-params)
                              middlewares/params
                              middlewares/keyword-params]
   {:post `cadastrar-cliente}])

