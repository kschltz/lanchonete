(ns user
  (:require
   [clojure.data.json :as json]
   [hato.client :as hc]
   [integrant.core :as ig]
   [integrant.repl :as r]
   [integrant.repl.state]
   [mba-fiap.lanchonete :as lanchonete]
   [migratus.core :as migratus]))

(integrant.repl/set-prep! #(lanchonete/prep-config :dev))

(def clear r/clear)
(def go r/go)
(def halt r/halt)
(def prep r/prep)
(def init r/init)
(def reset r/reset)
(def reset-all r/reset-all)

(defn migratus
  []
  (:mba-fiap.datasource.migratus/migratus integrant.repl.state/system))

(defn db
  []
  (:mba-fiap.datasource.postgres/db integrant.repl.state/system))

(defn nats []
  (:mba-fiap.adapter.nats/client integrant.repl.state/system))

(defn repository
  [repository-key]
  (->> (ig/find-derived integrant.repl.state/system :mba-fiap.repository.repository/repository)
       (filter (fn [[[_ rk]]] (= rk repository-key)))
       first
       second))

(comment
  (.listar (repository :repository/cliente) {})
  (.listar (repository :repository/produto) {})
  (.listar (repository :repository/pedido) {})
  (.listar (repository :repository/pagamento) {})

  (.criar (repository :repository/pedido)
          {:id-cliente #uuid "236d3142-e4a7-4c23-976c-34454d8db1fc",
           :produtos
           [#uuid "f11c6b18-89fb-461a-9d76-9c59d9262f23"
            #uuid "4e5ce39e-e30e-48e9-a763-f2a2f2fdcd68"
            #uuid "b800c75e-18af-4d31-a7f1-6f5b3a457903"],
           :numero-do-pedido "2",
           :total 2000,
           :status "aguardando pagamento"})

  (.atualizar (repository :repository/pedido)
              {:id #uuid"fbb98663-77ab-4560-a065-6b9b833c190f"
               :id-cliente #uuid "336d3142-e4a7-4c23-976c-34454d8db1fc",
               :produtos [#uuid "f11c6b18-89fb-461a-9d76-9c59d9262f23"]
               :numero-do-pedido "5",
               :total 2000,
               :status "aguardando pagamento"})

  (.criar (repository :repository/produto)
          {:nome "novo-produto"
           :descricao "descricao"
           :categoria :lanche
           :preco-centavos 400})

  (.criar (repository :repository/pagamento)
          {:id-pedido #uuid"f1429128-0418-4a87-b19a-b5454b167727"
           :total 12345
           :status "em processamento"}))

(defn add-migration
  [migration-name]
  (migratus/create (migratus) migration-name))

(defn url [& [host path]]
  (str (format "http://%s:8080" (or host "localhost")) path))

(defn post-client
  [& [host body]]
  (hc/post (url host "/cliente")
           {:throw-exceptions? false
            :headers {"content-type" "application/json"}
            :body (json/write-str (or body {"cpf" "04373360189"}))}))

(defn get-cliente
  [cpf & [host]]
  (hc/get (url host (str "/cliente/" cpf))))

(defn get-produtos
  [categoria]
  (-> (hc/get
       (str "http://localhost:8080/produtos/" categoria)
       {:throw-exceptions? false
        :headers {"Content-Type" "application/json"}})
      (doto tap>)))

(defn post-produto
  [& [host body]]
  (hc/post (url host "/produto")
           {:throw-exceptions? false
            :headers {"content-type" "application/json"}
            :body (json/write-str (or body
                                      {:nome "Sandubinha do bem"
                                       :descricao "Sandubinha do bem"
                                       :categoria "lanche"
                                       :preco-centavos 4400}))}))

(defn deletar-produto
  [id]
  (hc/delete (str "http://localhost:8080/produto/" id)))

(defn editar-produto
  [id]
  (hc/put (str "http://localhost:8080/produto/" id)
          {:throw-exceptions? false
           :headers {"content-type" "application/json"}
           :body (json/write-str {:nome "Novo sanduba editado"
                                  :descricao "Novo sanduba editado"
                                  :categoria "lanche"
                                  :preco-centavos 4750})}))

(defn portal
  []
  (eval '(do
           (require '[portal.api :as api])
           (add-tap api/submit)
           (api/open))))

(defn stress-cluster [external-ip n-req]
  (time
   (apply
    pcalls
    (repeat n-req
            #(do
               (let [start (System/currentTimeMillis)
                     res (hato.client/get (str "http://" external-ip ":8080/produtos/lanche"))
                     end (System/currentTimeMillis)]
                 {:response res
                  :duration (- end start)}))))))

(defn get-pedidos
  [& [host]]
  (hc/get (url host "/pedidos")))

(defn post-pedido
  [& [host body]]
  (hc/post (url host "/pedido")
           {:throw-exceptions? false
            :headers {"content-type" "application/json"}
            :body (json/write-str body)}))

(defn post-confirmacao-pagamento
  [id-pgmto & [host]]
  (hc/post
   (url host (str "/confirmacao-pagamento/" id-pgmto))
   {:throw-exceptions? false
    :headers {"content-type" "application/json"}
    :body (json/write-str {:status "pago"})}))

(defn ->body
  [response]
  (tap> response)
  (Thread/sleep 1)
  (-> response :body (json/read-str :key-fn keyword)))

(defn pedido-cycle [& [host]]
  (let [
        ;_
        #_(next.jdbc/execute! (db) [(str "TRUNCATE TABLE pedido CASCADE;"
                                       "TRUNCATE TABLE pagamento CASCADE;"
                                       "TRUNCATE TABLE produto CASCADE;"
                                       "TRUNCATE TABLE cliente CASCADE;")])
        cliente (->body (post-client host))
        bebida (->body (post-produto host {:nome           "Guaraná Jesus"
                                           :descricao      "coca rosa"
                                           :categoria      :bebida
                                           :preco-centavos 700}))
        acompanhamento (->body (post-produto host {:nome           "Batatas oleosas"
                                                   :descricao      "infarto potato"
                                                   :categoria      :acompanhamento
                                                   :preco-centavos 1500}))
        lanche (->body (post-produto host {:nome           "X-BEIKO"
                                           :descricao      "pancoporco"
                                           :categoria      :lanche
                                           :preco-centavos 3000}))

        pedido (->body (hc/post (url host "/pedido")
                                (doto {:body         (json/write-str {:produtos         (mapv :id [bebida acompanhamento lanche])
                                                                      :id-cliente       (:id cliente)
                                                                      :numero-do-pedido "1"
                                                                      :total            5200})
                                       :content-type :json}
                                  tap>)))]

    (doto {:cliente cliente
           :bebida bebida
           :acompanhamento acompanhamento
           :lanche lanche
           :pedido pedido} tap>)))

(comment
  (post-client "0.0.0.0")
  (get-pedidos)
  (pedido-cycle))
