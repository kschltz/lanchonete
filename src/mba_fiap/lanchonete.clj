(ns mba-fiap.lanchonete
  (:gen-class)
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [com.brunobonacci.mulog :as log]
    [integrant.core :as ig]
    [mba-fiap.datasource.cliente]))

(def ^:const system-filename "config.edn")
(defmethod aero.core/reader 'ig/ref
  [{:keys [profile] :as opts} _tag value]
  (integrant.core/ref value))
(defn read-config [profile]
  (aero/read-config (io/resource system-filename) {:profile profile}))

(defn prep-config [profile]
  (let [config-map (read-config profile)]
    (ig/load-namespaces config-map)
    (ig/prep config-map)))

(defn start-app [profile]
  (log/start-publisher! {:type :console})
  (-> (prep-config profile)
      (ig/init)))

(defn -main
  [& args]
  (let [profile (or (some-> args first keyword) :prod)]
    (println "Running, profile: " profile)
    (start-app profile)))
