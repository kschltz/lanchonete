(ns mba-fiap.model.pagamento)

(def em-processamento "em processamento")
(def pago "pago")
(def recusado "recusado")

(def Status
  [:enum
   em-processamento
   pago
   recusado])

(def Pagamento
  [:map
   [:id-pedido uuid?]
   [:total pos-int?]
   [:status Status]])
