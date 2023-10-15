(ns mba-fiap.repository.cliente)

(defprotocol ClienteRepository
  (criar [this cliente])
  (buscar [this id])
  (atualizar [this cliente])
  (remover [this id])
  (listar [this q]))
