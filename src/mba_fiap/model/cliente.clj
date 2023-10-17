(ns mba-fiap.model.cliente
  (:require
    [malli.core :as m]
    [mba-fiap.value-object.cpf :as cpf]
    [mba-fiap.value-object.email :as email]))

(def AnonymousCliente
  [:map {:closed true}])

(def CPFIdentifiedCliente
  [:map [:cpf cpf/CPF]])

(def EmailIdentifiedCliente
  [:map
   [:email email/Email]
   [:name :string]])

(def cpf-identified ::cpf-identified)
(def email-identified ::email-identified)
(def anonymous ::anonymous)

(def cliente-types
  {CPFIdentifiedCliente cpf-identified
   EmailIdentifiedCliente email-identified
   AnonymousCliente anonymous})


(def Cliente
  [:or
   CPFIdentifiedCliente
   EmailIdentifiedCliente
   AnonymousCliente])

(defn ->cliente-type [cliente]
  (->> cliente-types
       (reduce-kv (fn [acc k v]
                    (if (m/validate k cliente)
                      (conj acc v)
                      acc))
                  [])))


