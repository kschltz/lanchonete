(ns mba-fiap.adapter.cliente-rest
  (:require [mba-fiap.service.cliente :as cliente.service]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.ring-middlewares :as middlewares]))


(defn cadastrar-cliente [request]
  (let [repository (get-in request [:app-context :repository/cliente])
        data (:json-params request)
        result (cliente.service/cadastrar-cliente repository data)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body result}))

(defn buscar-por-cpf [request]
  (let [repository (get-in request [:app-context :repository/cliente])
        {:keys [cpf]} (:path-params request)
        result (cliente.service/buscar-por-cpf repository cpf)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body result}))

(defn cliente-routes []
  [["/cliente" ^:interceptors [(body-params/body-params)
                               middlewares/params
                               middlewares/keyword-params]
    {:post `cadastrar-cliente}]
   ["/cliente/:cpf"
    {:get `buscar-por-cpf}]])

