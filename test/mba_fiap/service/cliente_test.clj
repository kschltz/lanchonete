(ns mba-fiap.service.cliente-test
  (:require [clojure.test :refer :all]
            [malli.generator :as mg]
            [mba-fiap.service.cliente :as cliente.service]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mba-fiap.model.cliente :as cliente]
            [clojure.test.check.clojure-test :refer [defspec]])
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

(defspec all-valid-clientes-inserted 1000
  (prop/for-all [cliente (mg/generator cliente/Cliente)]
    (let [store (atom {})
          mr (mock-repository store)]
      (boolean (cliente.service/cadastrar-cliente mr cliente)))))

(defspec buscar-por-cpf-test 1000
  (prop/for-all [cliente (mg/generator cliente/CPFIdentifiedCliente)]
    (let [store (atom {})
          mr (mock-repository store)
          _inserted (cliente.service/cadastrar-cliente mr cliente)
          found (cliente.service/buscar-por-cpf mr (:cpf cliente))]
      (= (:cpf cliente) (:cpf found)))))

(defspec remover-cliente-test 1000
  (prop/for-all [cliente (mg/generator cliente/CPFIdentifiedCliente)]
    (let [store (atom {})
          mr (mock-repository store)
          _inserted (cliente.service/cadastrar-cliente mr cliente)]
      (cliente.service/remover-por-cpf mr (:cpf cliente))
      (nil? (cliente.service/buscar-por-cpf mr (:cpf cliente))))))