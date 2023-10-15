(ns mba-fiap.service.cliente
  (:require [malli.core :as m]
            [mba-fiap.base.validation :as validation]
            [mba-fiap.model.cliente :as cliente])
  (:import [mba_fiap.repository.repository Repository]))

(defn cadastrar-cliente [^Repository repository data]
  {:pre [(instance? Repository repository)
         (validation/schema-check cliente/Cliente data )]}
  (let [{:cliente/keys [id]} (.criar repository data)]
    {:id id}))
