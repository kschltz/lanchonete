(ns mba-fiap.system
  (:require [clj-test-containers.core :as tc]
            [clojure.test :refer :all]
            [integrant.core :as ig]
            [mba-fiap.lanchonete :as core]))

(defonce db-state (atom ::not-initialized))

(defn start-pg-container []
  (let [pg-container
        (-> (tc/create {:image-name    "postgres:16.3"
                        :exposed-ports [5432]
                        :env-vars      {"POSTGRES_PASSWORD" "password"
                                        "POSTGRES_USER"     "postgres"
                                        "POSTGRES_DB"       "postgres"}})
            (tc/bind-filesystem! {:host-path      "/tmp"
                                  :container-path "/opt"})
            (tc/start!))]
    (reset! db-state pg-container)
    pg-container))

(defn stop-pg-container []
  (tc/stop! @db-state)
  (reset! db-state ::not-initialized))



(defonce system-state (atom ::not-initialized))

(defn system-start []
  (let [conf (core/prep-config :test)
        conf (-> conf
                 (assoc-in [:mba-fiap.datasource.postgres/db :spec :host] (:host @db-state))
                 (assoc-in [:mba-fiap.datasource.postgres/db :spec :port] (get (:mapped-ports @db-state) 5432)))
        system (ig/init conf)]
    (tap> system)
    (reset! system-state system)
    system))

(defn system-stop []
  (ig/halt! @system-state)
  (reset! system-state ::not-initialized))

