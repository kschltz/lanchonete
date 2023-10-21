(ns mba-fiap.adapter.produto-rest
  (:require
    [io.pedestal.http.body-params :as body-params]
    [io.pedestal.http.ring-middlewares :as middlewares]
    [mba-fiap.service.produto :as produto.service]))


(defn listar-produtos
  [request]
  (tap> request)
  (let [_ (tap> request)
        repository (get-in request [:app-context :repository/produto])
        result (produto.service/listar-produto repository)]
    {:status 200
     :body result}))


(defn produto-routes
  []
  [["/produto" ^:interceptors [(body-params/body-params)]
    {:get `listar-produtos}]])
