(ns mba-fiap.lanchonete
  (:gen-class)
  (:require
    [aero.core :as aero]
    [mba-fiap.datasource.migratus]
    [clojure.java.io :as io]
    [integrant.core :as ig]))

(def ^:const system-filename "config.edn")
(defn read-config []
  (aero/read-config (io/resource system-filename)))

(defn start-app []
  (let [config-map (read-config)]
    (ig/prep config-map)
    (ig/init config-map)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (start-app))
