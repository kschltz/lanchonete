(ns mba-fiap.adapter.pagamento-rest
  (:require
   [io.pedestal.http.body-params :as body-params]
   [io.pedestal.http.ring-middlewares :as middlewares]
   [mba-fiap.service.pagamento :as pagamento.service]))

(defn buscar-pagamento-por-id-pedido
  [request]
  (let [repository (get-in request [:app-context :repository/pagamento])
        {:keys [id-pedido]} (:path-params request)
        id-pedido (parse-uuid id-pedido)
        result (pagamento.service/buscar-por-id-pedido repository id-pedido)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body result}))

(defn pagamento-routes
  []
  [["/pagamento/:id-pedido" ^:interceptors [(body-params/body-params)
                                            middlewares/params
                                            middlewares/keyword-params]
    {:get `buscar-pagamento-por-id-pedido}]])