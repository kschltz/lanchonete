(ns mba-fiap.value-object.cpf
  (:require [clojure.string :as str]
            [malli.core :as m]
            [clojure.spec.gen.alpha :as gen]))

(defn trim-cpf [cpf]
  (str/replace cpf #"[^\d]" ""))

(defn ->validator-digit [cpf]
  {:pre [(string? cpf)
         (<= 9 (count (trim-cpf cpf)) 10)]}
  (let [trimmed (trim-cpf cpf)
        input-size (count trimmed)]
    (->> (seq trimmed)
         (map-indexed
           (fn [i c] (* (Integer/parseInt (str c))
                        (- (inc input-size) i))))
         (reduce +)
         (* 10)
         (#(mod % 11))
         (#(if (> % 9) 0 %)))))

(defn valid? [cpf]
  (let [t (trim-cpf cpf)
        vd1 (->validator-digit (subs t 0 9))
        vd2 (->validator-digit (subs t 0 10))]
    (= (subs t 9 11) (str vd1 vd2))))

(defn gen-cpf []
  (let [digits (->> (repeatedly 9 #(rand-int 10))
                    (apply str))
        vd1 (->validator-digit digits)
        vd2 (->validator-digit (str digits vd1))]
    (str digits vd1 vd2)))

(def CPF
  (m/-simple-schema
    {:type ::CPF
     :pred valid?
     :type-properties {:error-message "CPF inválido"
                       :gen/gen (gen/fmap (fn [_] (gen-cpf)) (gen/return nil))}}))