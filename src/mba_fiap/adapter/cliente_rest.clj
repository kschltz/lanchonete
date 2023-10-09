(ns mba-fiap.adapter.cliente-rest
  (:require [mba-fiap.service.cliente :as cliente.service]))


(defn create-cliente [request]
  (let [repository (get-in request [:context :repository])
        ;; repositories loaded when app starts up via an interceptor or anything, really
        data (get-in request [:params :cliente])]
    (cliente.service/cadastra-cliente repository data)))

(defn routes []
  [["/cliente" {:post create-cliente}]
   ["/cliente/:id" {
                    ;:get read-cliente
                    ;:put update-cliente
                    ;:delete delete-cliente
                    }]])