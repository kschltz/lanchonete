(ns mba-fiap.model.pagamento)

(def Status
  [:enum "em processamento" "pago" "recusado"])

(def Pagamento
  [:map
   [:id-pedido uuid?]
   [:total pos-int?]
   [:status Status]])
