(ns mba-fiap.adapter.pedido-rest
  (:require
    [io.pedestal.http.body-params :as body-params]
    [io.pedestal.http.ring-middlewares :as middlewares]
    [mba-fiap.service.pedido :as pedido.service]
    [mba-fiap.usecase.listar-pedido :as usecase.p]))


(defn cadastrar-pedido
  [request]
  (let [repository (get-in request [:app-context :repository/pedido])
        data       (:json-params request)
        parsed-data (-> data
                        (update :id-cliente parse-uuid)
                        (update :produtos #(mapv parse-uuid %)))
        result     (pedido.service/checkout repository parsed-data)]
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body    result}))


(defn listar-pedidos
  [request]
  (let [repository (get-in request [:app-context :repository/pedido])
        result (pedido.service/listar-pedidos repository (usecase.p/listar-pedidos-abertos))]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body result}))


(defn pedido-routes
  []
  [["/pedido" ^:interceptors [(body-params/body-params)
                              middlewares/params
                              middlewares/keyword-params]
    {:post `cadastrar-pedido}]
   ["/pedidos" ^:interceptors [(body-params/body-params)
                               middlewares/params
                               middlewares/keyword-params]
    {:get `listar-pedidos}]])

