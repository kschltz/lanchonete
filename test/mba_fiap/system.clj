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



(defonce nats-state (atom ::not-initialized))
(defn start-nats-container
  []
  (let [nats-container
        (-> (tc/create {:image-name    "nats:2.10.14-alpine"
                        :exposed-ports [4222]})
            (tc/start!))]
    (reset! nats-state nats-container)

    nats-container))


(defn stop-nats-container
  []
  (tc/stop! @nats-state)
  (reset! nats-state ::not-initialized))


(defonce system-state (atom ::not-initialized))

(defn system-start []
  (let [_ (start-pg-container)
        _ (start-nats-container)
        conf (core/prep-config :test)
        conf (-> conf
                 (assoc-in [:mba-fiap.datasource.postgres/db :spec :host] (:host @db-state))
                 (assoc-in [:mba-fiap.datasource.postgres/db :spec :port] (get (:mapped-ports @db-state) 5432))
                 (assoc-in [:mba-fiap.adapter.nats/client :url]
                           (format "nats://%s:%s"
                                   (:host @nats-state)
                                   (get (:mapped-ports @nats-state) 4222))))
        system (ig/init conf)]
    (tap> system)
    (reset! system-state system)
    system))

(defn system-stop []
  (ig/halt! @system-state)
  (reset! system-state ::not-initialized)
  (stop-pg-container)
  (stop-nats-container))

