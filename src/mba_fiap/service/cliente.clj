(ns mba-fiap.service.cliente
  (:import [mba_fiap.repository.cliente ClienteRepository]))


(defn cadastrar-cliente [^ClienteRepository repository data]
  {:pre [(instance? ClienteRepository repository)
         (map? data)]}
  ;; do magick, validate stuff, etc
  (.create repository data))
