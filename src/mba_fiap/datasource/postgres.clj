(ns mba-fiap.datasource.postgres
  (:require [clojure.string :as str]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]))

(defn ^:private get-host [host-port]
  (first (str/split host-port #":")))

(defmethod ig/init-key ::db
  [_ {:keys [spec]
      :as component}]
  (println "Initializing database connection: " component)
  (let [updated-spec (update spec :host get-host)]
    (assoc
      component
      :datasource
      (jdbc/get-datasource updated-spec))))

(defmethod ig/resolve-key ::db
  [_ {:keys [datasource]}]
  datasource)
