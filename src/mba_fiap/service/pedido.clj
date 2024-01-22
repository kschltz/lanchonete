(ns mba-fiap.service.pedido
  (:require
    [mba-fiap.base.validation :as validation]
    [mba-fiap.model.pedido :as pedido])
  (:import
    (mba_fiap.repository.repository
      Repository)))


(defn array->vector
  [a]
  (if (coll? a)
    a
    (into [] (and a (.getArray a)))))


(defn ^:private pg->pedido
  [{:pedido/keys [id id_cliente numero_do_pedido produtos status total created_at]}]
  {:id id
   :id-cliente id_cliente
   :numero-do-pedido numero_do_pedido
   :produtos (array->vector produtos)
   :status status
   :total total
   :created-at created_at})


(defn checkout
  [^Repository repository data]
  {:pre [(instance? Repository repository)
         (validation/schema-check pedido/Pedido data)]}
  (let [[{:pedido/keys [id id_cliente numero_do_pedido produtos status total]}] (.criar repository data)]
    {:id id
     :id-cliente id_cliente
     :numero-do-pedido numero_do_pedido
     :produtos (array->vector produtos)
     :status status
     :total total}))


(defn listar-pedidos
  ([^Repository repository]
   {:pre [(instance? Repository repository)]}
   (listar-pedidos repository {}))
  ([^Repository repository query]
   {:pre [(instance? Repository repository)]}
   (let [result (.listar repository query)
         pedidos (mapv pg->pedido result)]
     pedidos)))

(def PedidoUpdate (validation/->update-schema pedido/Pedido))
(defn editar-pedido
  [^Repository repository data]
  {:pre [(instance? Repository repository)
         (validation/schema-check PedidoUpdate data)]}
  (let [[{:pedido/keys [id id_cliente numero_do_pedido produtos status total]}] (.atualizar repository data)]
    {:id id
     :id-cliente id_cliente
     :numero-do-pedido numero_do_pedido
     :produtos (into [] (.getArray produtos))
     :status status
     :total total}))
