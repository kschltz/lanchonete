(ns mba-fiap.adapter.cliente-rest
  (:require
   [io.pedestal.http.body-params :as body-params]
   [io.pedestal.http.ring-middlewares :as middlewares]
   [mba-fiap.service.cliente :as cliente.service]
   [mba-fiap.service.auth :as auth.service]))

(defn cadastrar-cliente
  [request]
  (let [repository (get-in request [:app-context :repository/cliente])
        data (:json-params request)
        result (cliente.service/cadastrar-cliente repository data)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body result}))

(defn buscar-por-cpf
  [request]
  (let [repository (get-in request [:app-context :repository/cliente])
        {:keys [cpf]} (:path-params request)
        result (cliente.service/buscar-por-cpf repository cpf)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body result}))

(defn autenticar-cliente
  [request]
  (let [repository (get-in request [:app-context :repository/cliente])
        lambda-url (get-in request [:app-context :auth])
        {:keys [cpf password]} (:json-params request)
        client-data (cliente.service/buscar-por-cpf repository cpf)
        email (:email client-data)]
    (if email
      (let [result (auth.service/autenticar lambda-url email password)]
        {:status 200
         :headers {"Content-Type" "application/json"}
         :body {:email email
                :token result}})
      {:status 400
       :headers {"Content-Type" "application/json"}
       :body {:error "The client does not exists in or system"}})))

(defn cliente-routes
  []
  [["/cliente"
    ^:interceptors [(body-params/body-params)
                    middlewares/params
                    middlewares/keyword-params]
    {:post `cadastrar-cliente}]

   ["/autenticar"
    ^:interceptors [(body-params/body-params)
                    middlewares/params
                    middlewares/keyword-params]
    {:post `autenticar-cliente}]

   ["/cliente/:cpf" {:get `buscar-por-cpf}]])
