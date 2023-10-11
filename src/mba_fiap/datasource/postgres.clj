(ns mba-fiap.datasource.postgres
  (:require [next.jdbc :as jdbc]
            [honey.sql :as hs]
            [integrant.core :as ig]
            [mba-fiap.repository.cliente :refer [ClienteRepository]]))

(defrecord ClienteDatasource [connection]
  ClienteRepository
  (criar [_ cliente]
    (jdbc/execute!
      connection
      (hs/format {:insert-into :cliente
                  :values {:cpf (:cpf cliente)
                           :nome (:nome cliente)
                           :email (:email cliente)}}))))



(defmethod ig/init-key ::db
  [_ {:keys [spec]
      :as component}]
  (assoc
    component
    :datasource
    (jdbc/get-datasource spec )))
