(ns mba-fiap.usecase.cliente-test
  (:require [clojure.test :refer :all]
            [mba-fiap.service.cliente :as cliente.service]
            [mba-fiap.usecase.cliente :as usecase.cliente])
  (:import [mba_fiap.repository.repository Repository]))

(defn mock-repository [store]
  (proxy [Repository] []
    (criar [data]
      (swap! store assoc (:cpf data) data)
      [#:cliente{:id (random-uuid)
                 :cpf (:cpf data)
                 :nome (:nome data)
                 :email (:email data)}])
    (buscar [cpf]
      (let [data (get @store cpf)]
        (when data
          #:cliente{:id (:id data)
                    :cpf (:cpf data)
                    :nome (:nome data)
                    :email (:email data)})))
    (remover [id]
      (swap! store dissoc id)
      nil)))


(deftest excluir-por-cpf-test
  (testing "Should remove cliente when it exists"
    (let [cliente {:cpf "10577604040"}
          repository (mock-repository (atom {}))
          _inserted (cliente.service/cadastrar-cliente repository cliente)
          response (usecase.cliente/excluir-por-cpf repository (:cpf cliente))]
      (is (= :success (:status response)))
      (is (= (:cpf cliente) (:cpf (:cliente response))))))

  (testing "Should return not-found when cliente does not exist"
    (let [repository (mock-repository (atom {}))
          cpf "10577604040"
          response (usecase.cliente/excluir-por-cpf repository cpf)]
      (is (= :not-found (:status response)))
      (is (nil? (:cliente response))))))
