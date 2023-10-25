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
    (listar [_q]
      (let [data @store]
        data))
    (criar [data]
      (swap! store assoc (:cpf data) data)
      [#:pedido{:id (random-uuid)
                :id-cliente (:id-cliente data)
                :numero-do-pedido (:numero_do_pedido data)
                :produtos (:produtos data)
                :status (:status data)
                :total (:total data)}])))

(defn gen-pedidos []
  (let [pedidos-schema [:vector {:min 1} pedido/Pedido]]
    (mg/generator pedidos-schema)))

(defspec all-valid-pedidos-inserted 2
  (prop/for-all [pedido (mg/generator pedido/Pedido)]
    (let [store (atom {})
          repository (mock-repository store)]
      (boolean (pedido.service/checkout repository pedido)))))

(defspec listar-pedidos-test 2
  (prop/for-all [pedidos (gen-pedidos)]
    (let [store (atom pedidos)
          repository (mock-repository store)]
      (boolean (pedido.service/listar-pedidos repository)))))
