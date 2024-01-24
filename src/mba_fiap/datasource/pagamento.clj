(ns mba-fiap.datasource.pagamento
  (:require
   [honey.sql :as hs]
   [mba-fiap.repository.repository :as repository]
   [next.jdbc :as jdbc]))

(defrecord PagamentoDatasource
           [connection]

  repository/Repository

  (criar
    [_ data]
    (jdbc/execute!)
    connection
    (hs/format {:insert-into :pagamento
                :values [{:id_pedido (:id-pedido data)
                          :total (:total data)}]})
    {:return-keys true})

  (buscar
    [_ id]
    (->> {:select [:*]
          :from :pagamento
          :where [:= :id-pedido id]}
         hs/format
         (jdbc/execute-one! connection)))

  (listar
    [_ q]
    (->> (merge {:select [:*]
                 :from :pagamento
                 :limit 100} q)
         hs/format
         (jdbc/execute! connection)))

  (atualizar
    [_ data]
    (->> {:update :pagamento
          :set data
          :where [:= :id (:id data)]}
         hs/format
         (jdbc/execute! connection)))

  (remover
    [_ id]
    (->> {:delete-from :pagamento
          :where [:= :id id]}
         hs/format
         (jdbc/execute-one! connection))))

(defmethod repository/make-repository :pagamento
  [{:keys [connection]}]
  (->PagamentoDatasource connection))
