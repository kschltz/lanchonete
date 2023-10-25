(ns mba-fiap.service.pedido
  (:require
    [mba-fiap.base.validation :as validation]
    [mba-fiap.model.pedido :as pedido])
  (:import
    [mba_fiap.repository.repository Repository]))

(defn checkout [^Repository repository data]
  {:pre [(instance? Repository repository)
         (validation/schema-check pedido/Pedido data)]}
  (println "chamou service checkout")
  (.criar repository data))

(defn listar-pedidos [^Repository repository]
  {:pre [(instance? Repository repository)]}
  (println "Listando")
  (.listar repository nil))
