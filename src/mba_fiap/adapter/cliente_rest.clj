(ns mba-fiap.adapter.cliente-rest
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.ring-middlewares :as middlewares]
            [mba-fiap.service.auth :as auth.service]
            [mba-fiap.service.cliente :as cliente.service]
            [mba-fiap.usecase.cliente :as usecase.cliente]))

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
        {:keys [cpf password email]} (:json-params request)
        client-data (cliente.service/buscar-por-cpf repository cpf)
        email (or (:email client-data) email)]
    (try
      (if-let [result (auth.service/autenticar lambda-url email password)]
        {:status  200
         :headers {"Content-Type" "application/json"}
         :body    {:email email
                   :token result}}

        {:status  400
         :headers {"Content-Type" "application/json"}
         :body    {:error "The client does not exists in our system"}})

      (catch Exception e
        {:status  500
         :headers {"Content-Type" "application/json"}
         :body    {:error (.getMessage e)}}))))

(defn excluir-cliente
  [request]
  (try 
    (let [repository (get-in request [:app-context :repository/cliente])
          {:keys [cpf]} (:path-params request)
          result (usecase.cliente/excluir-por-cpf repository cpf)]
      
       (case (:status result) 
         :success
         {:status  200
          :headers {"Content-Type" "application/json"}
          :body    (:cliente result)}

         :not-found
         {:status  404
          :headers {"Content-Type" "application/json"}
          :body    {:error "The client does not exists in our system"}}))
    
    (catch Exception e
      {:status  500
       :headers {"Content-Type" "application/json"}
       :body    {:error (.getMessage e)}})))


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

   ["/cliente/:cpf" {:get `buscar-por-cpf
                     :delete `excluir-cliente}]])
