(ns mba-fiap.model.pagamento)

(def Pagamento
  [:map
   [:id-pedido uuid?]
   [:total pos-int?]
   [:created-at string?]])
