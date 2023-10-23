(ns mba-fiap.model.produto)


(def Categoria
  [:enum "lanche" "sobremesa" "acompanhamento" "bebida"])


(def Produto
  [:map  [:nome string?]
   [:descricao {:optional true} string?]
   [:preco-centavos pos-int?]
   [:categoria Categoria]])
