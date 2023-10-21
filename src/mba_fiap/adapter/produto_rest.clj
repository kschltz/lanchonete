(ns mba-fiap.adapter.produto-rest
  (:require
    [io.pedestal.http.body-params :as body-params]
    [io.pedestal.http.ring-middlewares :as middlewares]
    [mba-fiap.service.produto :as produto.service]))


(defn listar-produtos
  [request]
  (let [_ (tap> request)
        repository (get-in request [:app-context :repository/produto])
        categoria (get-in request [:path-params :categoria])
        result (produto.service/listar-produto repository categoria)]
    {:status 200
     :body result}))


#_(defn criar-produto
  [request]
  (let [_ (tap> request)
        repository (get-in request [:app-context :repository/produto])
        result (produto.service/listar-produto repository)]
    {:status 200
     :body result}))


(defn produto-routes
  []
  [["/produto/:categoria" ^:interceptors [(body-params/body-params)]
    {:get `listar-produtos}]])
