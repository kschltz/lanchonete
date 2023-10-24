(ns mba-fiap.service.pedido
  (:require
    [mba-fiap.base.validation :as validation]
    [mba-fiap.model.pedido :as pedido])
  (:import
    [mba_fiap.repository.repository Repository]))

(defn checkout [^Repository repository data]
  {:pre [(instance? Repository repository)
         (validation/schema-check pedido/Pedido data)]}
  ;; TODO: Make checkout logic
  )

(defn listar-pedidos [^Repository repository]
  {:pre [(instance? Repository repository)]}
  (.listar repository {}))
