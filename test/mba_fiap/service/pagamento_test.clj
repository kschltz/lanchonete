(ns mba-fiap.service.pagamento-test
  (:require
   [clojure.test.check.clojure-test :refer [defspec]]
   [clojure.test.check.properties :as prop]
   [malli.generator :as mg]
   [mba-fiap.model.pagamento :as pagamento]
   [mba-fiap.service.pagamento :as pagamento.service]))

(defn mock-repository [store]
  (proxy [Repository] []
    (criar [data]
      (swap! store assoc (:id data) data)
      [#:pagamento{:id (random-uuid)
                   :id_pedido (:id_pedido data)
                   :total (:total data)
                   :created_at (:created_at data)}])
    (buscar [id]
      (let [data (get @store id)]
        (when data
          #:pagamento{:id_pedido (:id_pedido data)
                      :total (:total data)
                      :created_at (:created_at data)})))))

(defspec buscar-por-id-pedido-test 1000
  (prop/for-all [pagamento (mg/generator pagamento/Pagamento)]
                (let [store (atom {}) mr (mock-repository store)
                      _insert (.criar mr pagamento)
                      found (pagamento.service/buscar-por-id-pedido mr (:id_pedido pagamento))]
                  (= (:id_pedido pagamento) (:id_pedido found)))))

(comment (buscar-por-id-pedido-test))
