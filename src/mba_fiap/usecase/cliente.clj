(ns mba-fiap.usecase.cliente
  (:require
   [mba-fiap.base.validation :as validation]
   [mba-fiap.service.cliente :as cliente-service]
   [mba-fiap.value-object.cpf :as cpf])
  (:import [mba_fiap.repository.repository Repository]))


(defn excluir-por-cpf [^Repository repository cpf]
  {:pre [(instance? Repository repository)
         (validation/schema-check cpf/CPF cpf)]}
   (let [cliente (cliente-service/buscar-por-cpf repository cpf)]
    (if (empty? cliente)
      {:status :not-found
       :cliente cliente}
      (do
        (cliente-service/remover-por-cpf repository cpf)
        {:status :success
         :cliente cliente}))))