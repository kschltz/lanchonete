(ns mba-fiap.repository.cliente)

(defprotocol ClienteRepository
  (create [this cliente])
  (read [this id])
  (update [this cliente])
  (delete [this id])
  (list [this q]))
