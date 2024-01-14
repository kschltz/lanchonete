(ns mba-fiap.datasource.pedido
  (:require
    [honey.sql :as hs]
    [mba-fiap.repository.repository :as repository]
    [next.jdbc :as jdbc]))


(defrecord PedidoDatasource
  [connection]

  repository/Repository

  (criar
    [_ pedido]
    (jdbc/execute!
      connection
      (hs/format {:insert-into :pedido
                  :values [{:numero-do-pedido (:numero-do-pedido pedido)
                            :produtos         [:array (:produtos pedido)]
                            :id-cliente       (:id-cliente pedido)
                            :total            (:total pedido)
                            :status           (:status pedido)}]})
      {:return-keys true}))


  (buscar
    [_ id]
    (->> {:select [:*]
          :from   :pedido
          :where  [:= :id id]}
         hs/format
         (jdbc/execute-one! connection)))


  (listar
    [_ q]
    (->>
      (merge {:select [:*]
              :from :pedido
              :order-by [[[:case [:= :status "pronto"] 1
                           [:= :status "em preparo"] 2
                           [:= :status "recebido"] 3
                           :else 4]]]
              :limit 100} q)
      hs/format
      (jdbc/execute! connection)))


  (atualizar
    [_ data]
    (jdbc/execute!
      connection
      (hs/format {:update :pedido
                  :set data
                  :where [:= :id (:id data)]})
      {:return-keys true}))


  (remover
    [_ id]
    (->> {:delete-from :pedido
          :where [[:= :id id]]}
         hs/format
         (jdbc/execute-one! connection))))


(defmethod repository/make-repository :pedido
  [{:keys [connection]}]
  (->PedidoDatasource connection))

