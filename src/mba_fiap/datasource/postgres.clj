(ns mba-fiap.datasource.postgres
  (:require [honey.sql :as hs]
            [integrant.core :as ig]
            [mba-fiap.repository.cliente :refer [ClienteRepository]]
            [next.jdbc :as jdbc]))

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

(defmethod ig/resolve-key ::db
  [_ {:keys [datasource]}]
  datasource)
