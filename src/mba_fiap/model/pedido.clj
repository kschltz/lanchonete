(ns mba-fiap.model.pedido)

(def Produtos
  [:vector {:min 1 :max 4}
   uuid?])

(def recebido "recebido")
(def aguardando-pagamento "aguardando pagamento")
(def em-preparo "em preparo")
(def pronto "pronto")
(def finalizado "finalizado")

(def Status
  [:enum
   aguardando-pagamento
   recebido
   em-preparo
   pronto
   finalizado])

(def Pedido
  [:map
   [:id-cliente uuid?]
   [:produtos Produtos]
   [:numero-do-pedido string?]
   [:total pos-int?]
   [:status Status]
   [:created-at {:optional true} string?]])
