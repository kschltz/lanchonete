(ns mba-fiap.datasource.produto
  (:require
    [honey.sql :as hs]
    [mba-fiap.repository.repository :as repository]
    [next.jdbc :as jdbc]))


(defrecord ProdutoDatasource
  [connection]

  repository/Repository

  (criar
    [_ produto]
    (jdbc/execute!
      connection
      (hs/format {:insert-into :produto
                  :values [{:nome (:nome produto)
                            :descricao (:descricao produto)
                            :categoria (name (:categoria produto))
                            :preco_centavos (:preco-centavos produto)}]})
      {:return-keys true}))


  (buscar
    [_ id]
    (->> {:select [:*]
          :from :produto
          :where [:= :id id]}
         hs/format
         (jdbc/execute-one! connection)))


  (listar
    [_ q]
    (->> (merge {:select [:*]
                 :from :produto
                 :limit 100} q)
         hs/format
         (jdbc/execute! connection)))


  (atualizar
    [_ data]
    (jdbc/execute!
      connection
      (hs/format {:update :produto
                  :set data
                  :where [:= :id (:id data)]})
      {:return-keys true}))


  (remover
    [_ id]
    (->> {:delete-from :produto
          :where [[:= :id id]]}
         hs/format
         (jdbc/execute-one! connection))))


(defmethod repository/make-repository :produto
  [{:keys [connection]}]
  (->ProdutoDatasource connection))
