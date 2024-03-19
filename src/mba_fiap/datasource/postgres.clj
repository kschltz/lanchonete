(ns mba-fiap.datasource.postgres
  (:require [clojure.string :as str]
            [integrant.core :as ig]
            [next.jdbc :as jdbc])
  (:import [java.net URL]))

(defn ^:private ensure-http-protocol [url]
  (if (or (str/starts-with? url "http://")
          (str/starts-with? url "https://"))
    url
    (str "http://" url)))

(defn ^:private get-hostname [host-port]
  (let [endpoint (ensure-http-protocol host-port)]
    (.getHost (URL. endpoint))))

(defmethod ig/init-key ::db
  [_ {:keys [spec]
      :as component}]
  (println "Initializing database connection: " component)
  (println "\n\nHOST: " (try
                          (:host (update spec :host get-hostname))
                          (catch Exception e
                            e)))
  (let [updated-spec (update spec :host get-hostname)]

    (assoc
      component
      :spec updated-spec
      :datasource
      (jdbc/get-datasource updated-spec))))

(defmethod ig/resolve-key ::db
  [_ {:keys [datasource]}]
  datasource)
