(ns user
  (:require
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
        :preco 0.40}
))


(defn add-migration
  [migration-name]
  (migratus/create (migratus) migration-name))


(defn post-client
  []
  (hc/post "http://localhost:8080/cliente" {:headers {"Content-Type" "application/json"}
                                            :body "{\"cpf\": \"04373360189\"}"}))
