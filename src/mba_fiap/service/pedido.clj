(ns mba-fiap.service.pedido
  (:require [mba-fiap.base.validation :as validation]
            [mba-fiap.model.pedido :as pedido]))

(defn checkout [^Repository repository data]
  {:pre [(validation/schema-check pedido/Pedido data)]}
  ;; TODO: Make checkout logic
  )
