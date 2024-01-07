(ns mba-fiap.model.pedido)


(def Produtos
  [:vector {:min 1 :max 3}
   uuid?])


(def Status
  [:enum
   "aguardando pagamento"
   "recebido"
   "em preparo"
   "pronto"
   "finalizado"])


(def Pedido
  [:map
   [:id-cliente uuid?]
   [:produtos Produtos]
   [:numero-do-pedido string?]
   [:total pos-int?]
   [:status Status]
   [:created-at string?]])
