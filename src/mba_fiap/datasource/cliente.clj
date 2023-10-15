(ns mba-fiap.datasource.cliente
  (:require [honey.sql :as hs]
            [next.jdbc :as jdbc]
            [mba-fiap.repository.repository :as repository]))

(defrecord ClienteDatasource [connection]
  repository/Repository
  (criar [_ cliente]
    (jdbc/execute!
      connection
      (hs/format {:insert-into :cliente
                  :values [{:cpf (:cpf cliente)
                            :nome (:nome cliente)
                            :email (:email cliente)}]})
      {:return-keys true}))
  (buscar [_ id]
    (->> {:select [:*]
          :from :cliente
          :where [:= :id id]}
         hs/format
         (jdbc/execute-one! connection)))
  (listar [_ q]
    (->> (merge {:select [:*]
                 :from :cliente
                 :limit 100} q)
         hs/format
         (jdbc/execute! connection)))

  (atualizar [_ data]
    (->> {:update :cliente
          :set data
          :where [:= :id (:id data)]}
         hs/format
         (jdbc/execute! connection)))

  (remover [_ id]
    (->> {:delete-from :cliente
          :where [[:= :id id]]}
         hs/format
         (jdbc/execute-one! connection))))

(defmethod repository/make-repository :cliente
  [{:keys [connection]}]
  (->ClienteDatasource connection))

