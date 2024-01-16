(ns mba-fiap.usecase.listar-pedido
  (:require
    [mba-fiap.model.pedido :as m.pedido]))


(defn listar-pedidos-abertos
  []
  {:where [:not= :status m.pedido/finalizado]
   :order-by [[[:case [:= :status m.pedido/pronto] 1
                [:= :status m.pedido/em-preparo] 2
                [:= :status m.pedido/recebido] 3
                :else 4]]
              :created-at]})
