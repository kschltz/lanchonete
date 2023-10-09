(ns mba-fiap.model.cliente)

(def Cliente
  [:map
   [:id :uuid]
   [:cpj {:optional true} :string]
   [:nome {:optional true}:string]
   [:email {:optional true}:string]])
