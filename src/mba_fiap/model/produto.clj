(ns mba-fiap.model.produto
  (:require
    [malli.core :as m]))


(def Categorias
  [:one-of :lanche :bebida :acompanhamento :sobremesa])


(def Produto
  [:map  [:nome string?]
   [:descricao string?]
   [:preco float?]
   [:categoria [:enum :lanche :sobremesa :acompanhamento :bebida]]])


(defn valid?
  [product]
  (m/validate Produto product))


(comment
(def produto {:nome "X-Tudo"
         :descricao "Pão, carne, queijo, ovo, bacon, alface, tomate, maionese"
         :preco 25.00
         :categoria  (keyword "lanche")})
(valid? produto))
