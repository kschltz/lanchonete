(ns mba-fiap.adapter.cliente-rest
  (:require [mba-fiap.service.cliente :as cliente.service]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.ring-middlewares :as middlewares]))


(defn cadastrar-cliente [request]
  (let [_ (tap> request)
        repository (get-in request [:context :repository/cliente])
        ;; repositories loaded when app starts up via an interceptor or anything, really
        data (get-in request [:params :cliente])]
    (cliente.service/cadastrar-cliente repository data)
    {:status 200}))

(defn cliente-routes []
  [[["/cliente" ^:interceptors [(body-params/body-params)
                                middlewares/params
                                middlewares/keyword-params]
     {:post `cadastrar-cliente}]]])

