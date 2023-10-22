(ns mba-fiap.datasource.pedido
  (:require [honey.sql :as hs]
            [next.jdbc :as jdbc]
            [mba-fiap.repository.repository :as repository]))


(defrecord PedidoDatasource [connection]
  repository/Repository
  (criar [_ pedido]
    (jdbc/execute!
      connection
      (hs/format {:insert-into :pedido
                  :values [{:produtos (:produtos pedido)
                            :valor-total (:total pedido)
                            :status :aberto}]})
      {:return-keys true})))

(defmethod repository/make-repository :pedido
  [{:keys [connection]}]
  (->PedidoDatasource connection))

