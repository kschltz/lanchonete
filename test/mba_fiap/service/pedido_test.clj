(ns mba-fiap.service.pedido-test
  (:require
    [clojure.test :refer :all]
    [malli.generator :as mg]
    [mba-fiap.service.pedido :as pedido.service]
    [clojure.test.check.properties :as prop]
    [mba-fiap.model.pedido :as pedido]
    [clojure.test.check.clojure-test :refer [defspec]])
  (:import
    [mba_fiap.repository.repository Repository]))



(defn mock-repository [store]
  (proxy [Repository] []
    (listar [q]
      (let [data @store]
        data))))

(defn gen-pedidos []
  (let [pedidos-schema [:vector {:min 1} pedido/Pedido]]
    (mg/generator pedidos-schema)))

(defspec buscar-por-cpf-test 100
  (prop/for-all [pedidos (gen-pedidos)]
    (let [store (atom pedidos)
          repository (mock-repository store)]
      (boolean (pedido.service/listar-pedidos repository)))))
