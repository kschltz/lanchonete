(ns mba-fiap.adapter.produto-rest
  (:require
    [io.pedestal.http.body-params :as body-params]
    [io.pedestal.http.ring-middlewares :as middlewares]
    [mba-fiap.repository.repository :as repository]
    [mba-fiap.service.produto :as produto.service]))


(defn listar-produtos
  [request]
  (tap> [::11 request])
  (let [repository (get-in request [:app-context :repository/produto])
        categoria (get-in request [:path-params :categoria])
        result (produto.service/listar-produto repository categoria)]
    {:status 200
     :body result}))


(defn criar-produto
  [request]
  (let [repository (get-in request [:app-context :repository/produto])
        data (:json-params request)
        result (produto.service/criar-produto repository data)]
    {:status 200
     :body result}))


(defn deletar-produto
  [request]
  (let [repository (get-in request [:app-context :repository/produto])
        id (get-in request [:path-params :id])
        id (parse-uuid id)
        _ (produto.service/deletar-produto repository id)]
    {:status 200}))


(defn editar-produto
  [request]
  (let [repository (get-in request [:app-context :repository/produto])
        id (get-in request [:path-params :id])
        id (parse-uuid id)
        data (:json-params request)
        data (assoc data :id id)
        result (produto.service/editar-produto repository data)]
    {:status 200
     :body result}))


(defn produto-routes
  []
  [["/produtos/:categoria" ^:interceptors [(body-params/body-params)]
    {:get `listar-produtos}]
   ["/produto" ^:interceptors [(body-params/body-params)]
    {:post `criar-produto}]
   ["/produto/:id" ^:interceptors [(body-params/body-params)]
    {:delete `deletar-produto
     :put `editar-produto}]])
