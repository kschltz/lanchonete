(ns mba-fiap.model.produto
  (:require
    [malli.core :as m]))


(def Categorias
  [:enum :lanche :sobremesa :acompanhamento :bebida])


(def Produto
  [:map  [:nome string?]
   [:descricao {:optional true} string?]
   [:preco-centavos pos-int?]
   [:categoria Categorias]])
