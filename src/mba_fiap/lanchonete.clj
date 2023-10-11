(ns mba-fiap.lanchonete
  (:gen-class)
  (:require
    [aero.core :as aero]
    [mba-fiap.datasource.migratus]
    [clojure.java.io :as io]
    [integrant.core :as ig]
    [mba-fiap.datasource.postgres]))

(def ^:const system-filename "config.edn")

(defmethod aero.core/reader 'ig/ref
  [{:keys [profile] :as opts} _tag value]
  (integrant.core/ref value))
(defn read-config []
  (aero/read-config (io/resource system-filename)))

(defn start-app []
  (let [config-map (read-config)]
    (ig/prep config-map)
    (ig/init config-map)))

(defn -main
  [& args]
  (start-app))
