(ns mba-fiap.service.pedido
  (:require
    [mba-fiap.base.validation :as validation]
    [mba-fiap.model.pedido :as pedido])
  (:import
    [mba_fiap.repository.repository Repository]))

(defn ^:private pg->pedido
  [{:pedido/keys [id id_cliente numero_do_pedido produtos status total]}]
  {:id id
   :id-cliente id_cliente
   :numero-do-pedido numero_do_pedido
   :produtos (into [] (.getArray produtos))
   :status status
   :total total})

(defn checkout [^Repository repository data]
  {:pre [(instance? Repository repository)
         (validation/schema-check pedido/Pedido data)]}
  (let [[{:pedido/keys [id id_cliente numero_do_pedido produtos status total]}] (.criar repository data)]
    {:id id
     :id-cliente id_cliente
     :numero-do-pedido numero_do_pedido
     :produtos (into [] (.getArray produtos))
     :status status
     :total total}))

(defn listar-pedidos [^Repository repository]
  {:pre [(instance? Repository repository)]}
  (let [result (.listar repository {})
        pedidos (mapv pg->pedido result)]
    pedidos))
