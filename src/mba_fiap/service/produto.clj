(ns mba-fiap.service.produto
  (:import
    (mba_fiap.repository.repository
      Repository)))


(defn listar-produto
  [^Repository repository]
  {:pre [(instance? Repository repository)]}
  (.listar repository {}))
