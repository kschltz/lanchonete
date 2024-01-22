(ns mba-fiap.datasource.postgres
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]))

(defmethod ig/init-key ::db
  [_ {:keys [spec]
      :as component}]
  (println "Initializing database connection: " component)
  (assoc
    component
    :datasource
    (jdbc/get-datasource spec )))

(defmethod ig/resolve-key ::db
  [_ {:keys [datasource]}]
  datasource)
