(ns mba-fiap.service.cliente
  (:require [malli.core :as m]
            [mba-fiap.base.validation :as validation]
            [mba-fiap.model.cliente :as cliente]
            [mba-fiap.value-object.cpf :as cpf])
  (:import [mba_fiap.repository.repository Repository]))

(defn cadastrar-cliente [^Repository repository data]
  {:pre [(instance? Repository repository)
         (validation/schema-check cliente/Cliente data)]}
  (let [[{:cliente/keys [id]}] (.criar repository data)]
    {:id id}))

(defn buscar-por-cpf [^Repository repository cpf]
  {:pre [(instance? Repository repository)
         (validation/schema-check cpf/CPF cpf)]}
  (let [{:cliente/keys [id nome email cpf]} (.buscar repository cpf)]
    {:id id
     :nome nome
     :cpf cpf
     :email email}))
