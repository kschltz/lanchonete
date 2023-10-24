(ns mba-fiap.datasource.pedido
  (:require
    [honey.sql :as hs]
    [next.jdbc :as jdbc]
    [mba-fiap.repository.repository :as repository]))


(defrecord PedidoDatasource [connection]
  repository/Repository
  (criar [_ pedido]
    (jdbc/execute!
      connection
      (hs/format {:insert-into :pedido
                  :values [{:produtos (:produtos pedido)
                            :cpf (:cpf pedido)
                            :total (:total pedido)
                            :status :aberto}]})
      {:return-keys true}))
  (listar [_ q]
    (->> (merge {:select [:*]
                 :from :pedido
                 :limit 100} q)
         hs/format
         (jdbc/execute! connection))))

(defmethod repository/make-repository :pedido
  [{:keys [connection]}]
  (->PedidoDatasource connection))

