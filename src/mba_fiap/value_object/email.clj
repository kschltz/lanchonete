(ns mba-fiap.value-object.email
  (:require [clojure.spec.gen.alpha :as gen]
            [malli.core :as m]))

(def email-regex #"^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$")

(defn valid? [email]
  (re-matches email-regex email))

(defn gen-email []
  (gen/fmap (fn [s] (str s "@email.com")) (gen/string-alphanumeric)))

(def Email
  (m/-simple-schema
    {:type ::EMAIL
     :pred valid?
     :type-properties {:error-message "Email inválido"
                       :gen/gen (gen-email)}}))
