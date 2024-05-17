(ns mba-fiap.datasource.migratus
  (:require
    [integrant.core :as ig]
    [migratus.core :as migratus]))

(defmethod ig/init-key ::migratus
  [_ {:keys [migrate-on-init? reset-on-init?]
      :or   {migrate-on-init? true}
      :as   component}]
  (when reset-on-init?
    (migratus/reset component))

  (when migrate-on-init?
    (migratus/migrate component))
  component)

