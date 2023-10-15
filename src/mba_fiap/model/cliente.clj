(ns mba-fiap.model.cliente)

(def Cliente
  [:map
   [:cpj {:optional true} :string]
   [:nome {:optional true}:string]
   [:email {:optional true}:string]])
