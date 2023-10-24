(ns mba-fiap.model.pedido)


(def Produtos
  [:vector {:min 1 :max 3}
   [:and int? [:> 0]]])

(def Status
  [:enum
   "aguardando pagamento"
   "recebido"
   "em preparo"
   "pronto"
   "finalizado"])

(def Pedido
  [:map
   [:cpf {:optional true} string?]
   [:produtos Produtos]
   [:total pos-int?]
   [:status Status]])
