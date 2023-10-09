(ns mba-fiap.datasource.cliente-pg
  (:require [next.jdbc :as jdbc]
            [honey.sql :as hs]
            [mba-fiap.repository.cliente :refer [ClienteRepository]]))

(defrecord PostgreDataSource [connection]
  ClienteRepository
  (create [_ cliente]
    (jdbc/execute!
      connection
      (hs/format {:insert-into :cliente
                  :values {:cpf (:cpf cliente)
                           :nome (:nome cliente)
                           :email (:email cliente)}}))))
