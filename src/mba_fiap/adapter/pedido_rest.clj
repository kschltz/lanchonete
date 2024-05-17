(ns mba-fiap.adapter.pedido-rest
  (:require
   [io.pedestal.http.body-params :as body-params]
   [io.pedestal.http.ring-middlewares :as middlewares]
   [mba-fiap.service.pedido :as pedido.service]
   [mba-fiap.service.produto :as produto.service]
   [mba-fiap.usecase.pedido :as usecase.p]
   [medley.core :as medley]))

(defn cadastrar-pedido
  [request]
  (try (let [repository-pedido (get-in request [:app-context :repository/pedido])
             repository-produto (get-in request [:app-context :repository/produto])
             data       (:json-params request)
             parsed-data (-> data
                             (update :id-cliente parse-uuid)
                             (update :produtos #(mapv parse-uuid %)))
             produtos   (produto.service/listar-por-ids repository-produto (:produtos parsed-data))
             to-create (usecase.p/criar-pedido produtos parsed-data)
             result     (pedido.service/checkout repository-pedido to-create)]
         {:status  200
          :headers {"Content-Type" "application/json"}
          :body    result})
       (catch Exception e
         {:status  500
          :headers {"Content-Type" "application/json"}
          :body    (str e)})))

(defn listar-pedidos
  [request]
  (let [repository (get-in request [:app-context :repository/pedido])
        result (pedido.service/listar-pedidos repository (usecase.p/listar-pedidos-abertos))]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body result}))

(defn editar-pedido
  [request]
  (let [repository (get-in request [:app-context :repository/pedido])
        id (get-in request [:path-params :id])
        data (:json-params request)
        parsed-data (-> data
                        (assoc :id id)
                        (update :id parse-uuid)
                        (medley/update-existing :id-cliente parse-uuid)
                        (medley/update-existing :produtos #(mapv parse-uuid %)))
        result (pedido.service/editar-pedido repository parsed-data)]
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
    {:get `listar-pedidos}]

   ["/pedido/:id" ^:interceptors [(body-params/body-params)
                                  middlewares/params
                                  middlewares/keyword-params]
    {:put `editar-pedido}]])

