(ns mba-fiap.model.pedido
  (:require
    [malli.core :as m]))


(def Produtos
  [:map
   [:id :uuid]])

(def Pedido
  [[:id string?]
   [:produtos Produtos]
   [:total int?]])
