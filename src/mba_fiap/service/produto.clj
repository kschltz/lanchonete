(ns mba-fiap.service.produto
  (:require
    [mba-fiap.base.validation :as validation]
    [mba-fiap.model.produto :as produto])
  (:import
    (mba_fiap.repository.repository
      Repository)))


(defn listar-produto
  [^Repository repository categoria]
  {:pre [(instance? Repository repository)
         (or (nil? categoria)
             (validation/schema-check produto/Categoria categoria))]}
  (.listar repository {:where (if categoria
                                [:= :categoria (name categoria)]
                                [])
                       :order-by [[:categoria :asc]]}))


(defn criar-produto
  [^Repository repository data]
  {:pre [(instance? Repository repository)
         (validation/schema-check produto/Produto data)]}
  (let [[{:produto/keys [id nome descricao categoria preco-centavos]}] (.criar repository data)]
    {:id id
     :nome nome
     :descricao descricao
     :categoria categoria
     :preco-centavos preco-centavos}))
