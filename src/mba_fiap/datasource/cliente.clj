(ns mba-fiap.datasource.cliente
  (:require [honey.sql :as hs]
            [next.jdbc :as jdbc]
            [mba-fiap.repository.repository :as repository])
  (:import [java.io Closeable]))

(defn uuid-parseable? [s]
  (boolean (try
             (parse-uuid s)
             (catch Exception _e
               false))))

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
  (buscar [_ id-or-cpf]
   (let [where-clause (cond
                        (uuid? id-or-cpf)  [:= :id id-or-cpf]
                        (uuid-parseable? id-or-cpf) [:= :id (parse-uuid id-or-cpf)]
                        :else [:= :cpf id-or-cpf])]
    (->> {:select [:*]
          :from :cliente
          :where where-clause}
         hs/format
         (jdbc/execute-one! connection))))
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

  (remover [_ id-or-cpf]
    (let [where-clause (cond
                         (uuid? id-or-cpf) [:= :id id-or-cpf]
                         (uuid-parseable? id-or-cpf) [:= :id (parse-uuid id-or-cpf)]
                         :else [:= :cpf id-or-cpf])]
      (->> {:delete-from :cliente
            :where       where-clause}
           hs/format
           (jdbc/execute-one! connection)))))

(defmethod repository/make-repository :cliente
  [{:keys [connection]}]
  (->ClienteDatasource connection))

