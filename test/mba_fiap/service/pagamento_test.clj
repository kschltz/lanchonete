(ns mba-fiap.service.pagamento-test
  (:require
   [clojure.test.check.clojure-test :refer [defspec]]
   [clojure.test.check.properties :as prop]
   [malli.generator :as mg]
   [mba-fiap.model.pagamento :as pagamento]
   [mba-fiap.service.pagamento :as pagamento.service])
  (:import [mba_fiap.repository.repository Repository]))

(defn mock-repository [store]
  (proxy [Repository] []
    (criar [data]
      (swap! store assoc (:id data) data)
      [#:pagamento{:id_pedido (random-uuid)
                   :total (:total data)
                   :created_at (:created_at data)}])
    (buscar [id]
      (let [data (get @store id)]
        (when data
          #:pagamento{:id_pedido (:id_pedido data)
                      :total (:total data)
                      :created_at (:created_at data)})))))

(defspec buscar-por-id-pedido-test 1000
  (prop/for-all
   [pagamento (mg/generator pagamento/Pagamento)]
   (let [store (atom {})
         mr (mock-repository store)
         _insert (.criar mr pagamento)
         found (pagamento.service/buscar-por-id-pedido mr (:id-pedido pagamento))]
     (= (:id_pedido pagamento) (:id_pedido found)))))

(defspec buscar-por-id-pedido-test-error 1000
  (prop/for-all
   [pagamento (mg/generator pagamento/Pagamento)]
   (let [store (atom {})
         mr (mock-repository store)
         _insert (.criar mr pagamento)
         found (pagamento.service/buscar-por-id-pedido mr (:id-pedido pagamento))]
     (= "Pagamento não encontrado" (:error found)))))

