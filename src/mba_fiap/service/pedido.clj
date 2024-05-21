(ns mba-fiap.service.pedido
  (:require
    [clojure.edn :as edn]
    [integrant.core :as ig]
    [mba-fiap.base.validation :as validation]
    [mba-fiap.model.pedido :as pedido]
    [mba-fiap.usecase.pedido :as pedido.use-case])
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
  {:id               id
   :id-cliente       id_cliente
   :numero-do-pedido numero_do_pedido
   :produtos         (array->vector produtos)
   :status           status
   :total            total
   :created-at       created_at})


(defn checkout
  [^Repository repository data]
  {:pre [(instance? Repository repository)
         (validation/schema-check pedido/Pedido data)]}
  (let [[{:pedido/keys [id id_cliente numero_do_pedido produtos status total]}] (.criar repository data)]
    {:id               id
     :id-cliente       id_cliente
     :numero-do-pedido numero_do_pedido
     :produtos         (array->vector produtos)
     :status           status
     :total            total}))

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
    {:id               id
     :id-cliente       id_cliente
     :numero-do-pedido numero_do_pedido
     :produtos         (into [] (.getArray produtos))
     :status           status
     :total            total}))

(defmethod ig/init-key ::checkout [_ {:keys [nats repository subject pagamento-status pedido-novo-preparo]}]
  (fn [r data]
    (.subscribe nats pagamento-status (fn [msg]
                                        (let [data (edn/read-string (String. (.getData msg)))
                                              paid (pedido.use-case/aguardar-pagamento data)]
                                          (when paid (let [updated (editar-pedido (or r repository) paid)]
                                                       (.publish nats pedido-novo-preparo updated))))))
    (let [created (checkout (or r repository) data)]
      (.publish nats subject (str created))
      created)))


(defmethod ig/init-key ::atualizar-status [_ {:keys [nats repository pagamento-status pedido-status]}]
  (.subscribe nats
              pedido-status
              (fn [msg]
                (->> (edn/read-string (String. (.getData msg)))
                     (editar-pedido repository))))
  (.subscribe nats
              pagamento-status
              (fn [msg]
                (->> (edn/read-string (String. (.getData msg)))
                     (editar-pedido repository)))))
