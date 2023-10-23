(ns mba-fiap.base.validation
  (:require
    [malli.core :as m]
    [malli.error :as me]
    [malli.util :as mu])
  (:import
    (clojure.lang
      ExceptionInfo)))


(defn business-error?
  [x]
  (and (instance? ExceptionInfo x)
       (= :business-error (:error-type (ex-data x)))))


(defn ->businessError
  [x]
  (throw (cond
           (string? x) (ex-info x {:error-type :business-error})
           (instance? Exception x) (ex-info (.getMessage x) {:error-type :business-error})
           :else (ex-info "Erro desconhecido" {:error-type :business-error}))))


(defn schema-check
  [schema x]
  (if (m/validate schema x)
    x
    (->businessError
      (str
        (me/humanize (m/explain schema x)) "\n"
        x))))


(defn ->update-schema
  [schema]
  (-> (mu/optional-keys schema)
      (mu/assoc :id uuid?)))

