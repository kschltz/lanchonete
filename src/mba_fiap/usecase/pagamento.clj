(ns mba-fiap.usecase.pagamento)


(defn listar-por-pedido-id
  [id]
  {:pre [(uuid? id)]}
  {:where [:= :id-pedido id]})

