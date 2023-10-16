(ns mba-fiap.repository.repository
  (:require [com.brunobonacci.mulog :as log]
            [integrant.core :as ig]))

(defprotocol Repository
  (criar [this data])
  (buscar [this id])
  (atualizar [this data])
  (remover [this id])
  (listar [this q]))

(defmulti make-repository :repository-name)

(defmethod make-repository :default
  [cfg]
  (log/log :repository/creation-failed
           :level :error
           :config cfg))

(defmethod ig/init-key ::repository [_ {:keys [repository-name connection]}]
  (make-repository {:repository-name repository-name
                    :connection connection}))