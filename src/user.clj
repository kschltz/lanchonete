(ns user
  (:require
    [mba-fiap.lanchonete :as lanchonete]
    [integrant.repl.state]
    [integrant.repl :refer [clear go halt prep init reset reset-all]]
    [migratus.core :as migratus]))

(integrant.repl/set-prep! #(lanchonete/prep-config))

(defn migratus []
  (:mba-fiap.datasource.migratus/migratus integrant.repl.state/system))

(defn db []
  (:mba-fiap.datasource.postgres/db integrant.repl.state/system))

(defn add-migration [migration-name]
  (migratus/create (migratus) "migaration-name"))