(ns mba-fiap.service.pagamento
  (:import
   [mba_fiap.repository.repository Repository]))

(defn buscar-por-id-pedido [^Repository repository id]
  {:pre [(instance? Repository repository)
         (uuid? id)]}
  (let [{:pagamento/keys [id_pedido total created_at]} (.buscar repository id)]
    (if (nil? id_pedido)
      {:error "Pagamento não encontrado"}

      {:id-pedido id_pedido
       :total total
       :created-at created_at})))
