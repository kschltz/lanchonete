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


(defn repository
  [repository-key]
  (->> (ig/find-derived integrant.repl.state/system :mba-fiap.repository.repository/repository)
       (filter (fn [[[_ rk]]] (= rk repository-key)))
       first
       second))


(comment
(.listar (repository :repository/cliente) {})
(.listar (repository :repository/produto) {})
(.criar (repository :repository/produto)
        {:nome "novo-produto"
        :descricao "descricao"
        :categoria :lanche
        :preco-centavos 400}
))


(defn add-migration
  [migration-name]
  (migratus/create (migratus) migration-name))


(defn post-client
  []
  (hc/post "http://localhost:8080/cliente" {:headers {"content-type" "application/json"}
                                            :body "{\"cpf\": \"04373360189\"}"}))


(defn get-cliente
  [cpf]
  (hc/get (str "http://localhost:8080/cliente/" cpf)))


(defn get-produtos
  [categoria]
  (-> (hc/get
        (str "http://localhost:8080/produto/" categoria)
        {:headers {"Content-Type" "application/json"}})
      (doto tap>)))


(defn criar-produtos
  []
  (hc/post "http://localhost:8080/produto" {:headers {"content-type" "application/json"}
                                            :body (json/write-str {:nome "Sandubinha do bem"
                                                                   :descricao "Sandubinha do bem"
                                                                   :categoria "lanche"
                                                                   :preco-centavos 4400})}))


(defn portal
  []
  (eval '(do
           (require '[portal.api :as api])
           (add-tap api/submit)
           (api/open))))

