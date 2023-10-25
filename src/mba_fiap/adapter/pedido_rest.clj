(ns mba-fiap.adapter.pedido-rest
  (:require
    [mba-fiap.service.pedido :as pedido.service]
    [io.pedestal.http.body-params :as body-params]
    [io.pedestal.http.ring-middlewares :as middlewares]))


(defn cadastrar-pedido [request]
  (println "chamado cadastrar")
  (let [_ (println "app-context " (:app-context request))
        repository (get-in request [:app-context :repository/pedido])
        _ (println "chamada p'os repo " repository)
        data       (:json-params request)
        _ (println "chamada pos data " data)
        result     (pedido.service/checkout repository data)]
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body    result}))

(defn listar-pedidos [request]
  (println "chamou?")
  (let [repository (get-in request [:app-context :repository/pedido])
        result (pedido.service/listar-pedidos repository)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body result}))

(defn pedido-routes []
  [["/pedido" ^:interceptors [(body-params/body-params)
                               middlewares/params
                               middlewares/keyword-params]
    {:post `cadastrar-pedido}]
   ["/pedidos" ^:interceptors [(body-params/body-params)
                               middlewares/params
                               middlewares/keyword-params]
    {:get `listar-pedidos}]])

