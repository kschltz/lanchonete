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


(def ProdutoUpdate (validation/->update-schema produto/Produto))


(defn editar-produto
  [^Repository repository data]
  {:pre [(instance? Repository repository)
         (validation/schema-check ProdutoUpdate data)]}
  (let [[{:produto/keys [id nome descricao categoria preco_centavos]}] (.atualizar repository data)]
    {:id id
     :nome nome
     :descricao descricao
     :categoria categoria
     :preco-centavos preco_centavos}))


(defn criar-produto
  [^Repository repository data]
  {:pre [(instance? Repository repository)
         (validation/schema-check produto/Produto data)]}
  (let [[{:produto/keys [id nome descricao categoria preco_centavos]}] (.criar repository data)]
    {:id id
     :nome nome
     :descricao descricao
     :categoria categoria
     :preco-centavos preco_centavos}))


(defn deletar-produto
  [^Repository repository id]
  {:pre [(instance? Repository repository)
         (uuid? id)]}
  (.remover repository id))
