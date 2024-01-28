(ns mba-fiap.service.pagamento-test
  (:require
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.properties :as prop]
    [malli.generator :as mg]
    [mba-fiap.model.pagamento :as pagamento]
    [mba-fiap.service.pagamento :as pagamento.service])
  (:import
    (java.time
      LocalDateTime)
    (mba_fiap.repository.repository
      Repository)))


(defn mock-repository
  [store]
  (proxy [Repository] []
    (criar
      [data]
      [#:pagamento{:id (random-uuid)
                   :id_pedido  (:id-pedido data)
                   :total      (:total data)
                   :status     (:status data)
                   :created_at (:created_at data)}])

    (buscar
      [id]
      (let [data (get @store id)]
        (when data
          #:pagamento{:id (:id data)
                      :id_pedido  (:id_pedido data)
                      :total      (:total data)
                      :status     (:status data)
                      :created_at (:created_at data)})))

    (listar
      [_q]
      (let [data @store]
        data))

    (atualizar
      [data]
      (let [found (get @store (:id data))
            updated-data (assoc found :status (:status data))]
        (swap! store assoc (:id updated-data) updated-data)
        [#:pagamento{:id (:id updated-data)
                     :id_pedido  (:id_pedido updated-data)
                     :total      (:total updated-data)
                     :status     (:status updated-data)
                     :created_at (:created_at updated-data)}]))))


(defspec criar-pagamento-test 1000
  (prop/for-all
    [pagamento (mg/generator pagamento/Pagamento)]
    (let [store (atom {}) mr (mock-repository store)
          result (pagamento.service/criar-pagamento mr pagamento)]
      (= (:id-pedido pagamento) (:id-pedido result)))))


(defspec buscar-por-id-pedido-test 1000
  (prop/for-all
    [pagamento (mg/generator pagamento/Pagamento)]
    (let [store (atom {})
          mr (mock-repository store)
          _insert (.criar mr pagamento)
          result (pagamento.service/buscar-por-id-pedido mr (:id-pedido pagamento))
          [{:pagamento/keys [id_pedido]}] result]
      (= (:id_pedido pagamento) id_pedido))))


(defspec buscar-por-id-pedido-test-error 1000
  (prop/for-all
    [pagamento (mg/generator pagamento/Pagamento)]
    (let [store (atom {})
          mr (mock-repository store)
          found (pagamento.service/buscar-por-id-pedido mr (:id-pedido pagamento))]
      (= "Pagamento não encontrado" (:error found)))))


(defspec atualizar-status-pagamento 1
  (prop/for-all
    [pagamento (mg/generator pagamento/Pagamento)]
    (let [store (atom {})
          mr (mock-repository store)
          _insert (.criar mr pagamento)
          found (pagamento.service/atualizar-status-pagamento mr (:id-pedido pagamento) "pago")]
      (= "pago" (:status found)))))

