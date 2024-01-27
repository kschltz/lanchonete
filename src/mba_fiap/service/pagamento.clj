(ns mba-fiap.service.pagamento
  (:require
    [mba-fiap.base.validation :as validation]
    [mba-fiap.model.pagamento :as pagamento])
  (:import
    [mba_fiap.repository.repository Repository]))

(defn buscar-por-id-pedido [^Repository repository id]
  {:pre [(instance? Repository repository)
         (uuid? id)]}
  (let [{:pagamento/keys [id_pedido total status created_at]} (.buscar repository id)]
    (if (nil? id_pedido)
      {:error "Pagamento não encontrado"}

      {:id-pedido id_pedido
       :total total
       :status status
       :created-at created_at})))

(defn atualizar-status-pagamento [^Repository repository id-pedido status]
  {:pre [(instance? Repository repository)
         (uuid? id-pedido)
         (validation/schema-check pagamento/Status status)]}
  (let [data {:id-pedido id-pedido :status status}
        [{:pagamento/keys [id_pedido created_at status total] :as pg}] (.atualizar repository data)]
    {:id-pedido  id_pedido
     :total      total
     :status     status
     :created-at created_at}))
