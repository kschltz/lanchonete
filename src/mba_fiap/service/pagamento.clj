(ns mba-fiap.service.pagamento
  (:require
   [mba-fiap.base.validation :as validation]
   [mba-fiap.model.pagamento :as pagamento])
  (:import
   [mba_fiap.repository.repository Repository]))

(defn ^:private pg->pagamento [{:pagamento/keys [id id_pedido total status created_at]}]
  {:id id
   :id-pedido id_pedido
   :total total
   :status status
   :created-at created_at})

(defn criar-pagamento [^Repository repository pagamento]
  {:pre [(instance? Repository repository)
         (validation/schema-check pagamento/Pagamento pagamento)]}
  (let [[{:pagamento/keys [id id_pedido total status created_at]}] (.criar repository pagamento)]
    {:id id
     :id-pedido  id_pedido
     :total      total
     :status     status
     :created-at created_at}))

(defn buscar-por-id-pedido [^Repository repository idBusca]
  {:pre [(instance? Repository repository)
         (uuid? idBusca)]}
  (let [result (.listar repository {:where [:= :id-pedido idBusca]})
        pagamentos (mapv pg->pagamento result)]
    (if (empty? pagamentos)
      {:error "Pagamento não encontrado"}
      pagamentos)))

(defn atualizar-status-pagamento [^Repository repository id-pedido status]
  {:pre [(instance? Repository repository)
         (uuid? id-pedido)
         (validation/schema-check pagamento/Status status)]}
  (let [data {:id-pedido id-pedido :status status}
        [{:pagamento/keys [id id_pedido created_at status total] :as pg}] (.atualizar repository data)]
    {:id id
     :id-pedido  id_pedido
     :total      total
     :status     status
     :created-at created_at}))
