(ns mba-fiap.service.produto-test
  (:require
   [clojure.test.check.clojure-test :refer [defspec]]
   [clojure.test.check.generators :as gen]
   [clojure.test.check.properties :as prop]
   [malli.core :as m]
   [malli.generator :as mg]
   [mba-fiap.model.produto :as produto]
   [mba-fiap.service.produto :as produto.service])
  (:import
   (mba_fiap.repository.repository
    Repository)))

(defn mock-repository
  []
  (proxy [Repository] []
    (listar
      [query]
      query)))

(defn valid-listar-produto-query
  [categoria]
  [:map
   [:where
    [:or
     [:= []]
     [:= [:= :categoria categoria]]]]
   [:order-by [:= [[:categoria :asc]]]]])

(defn valid-query?
  [categoria]
  (m/validator (valid-listar-produto-query categoria)))

(defspec listar-produto-query-test 100
  (prop/for-all
   [categoria (gen/one-of [(mg/generator produto/Categoria) (gen/return nil)])]
   (let [valid? (valid-query? categoria)]
     (valid? (produto.service/listar-produto (mock-repository) categoria)))))

