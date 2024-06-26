(ns mba-fiap.usecase.pedido
  (:require
   [mba-fiap.model.pedido :as m.pedido]
   [mba-fiap.model.pagamento :as m.pagamento]))

(defn listar-pedidos-abertos
  []
  {:where [:not= :status m.pedido/finalizado]
   :order-by [[[:case [:= :status m.pedido/pronto] 1
                [:= :status m.pedido/em-preparo] 2
                [:= :status m.pedido/recebido] 3
                :else 4]]
              :created-at]})

(defn criar-pedido [produtos-data pedido-data]
  (let [total (->> produtos-data
                   (map :produto/preco_centavos)
                   (reduce +))
        number-products (count produtos-data)
        errors (cond-> {}
                 (not= total (:total pedido-data))
                 (assoc :error/total (str "O valor informado está incorreto: " total))

                 (not= number-products (count (:produtos pedido-data)))
                 (assoc :error/produtos (str "A quantidade de produtos informada está incorreta: "
                                             number-products)))]

    (when (seq errors)
      (throw (ex-info "O pedido contêm erros: " errors)))
    {:id-cliente (:id-cliente pedido-data)
     :produtos (mapv :produto/id produtos-data)
     :numero-do-pedido (:numero-do-pedido pedido-data)
     :total total
     :status m.pedido/recebido}))

(defn aguardar-pagamento [pagamento]
  (if (= (:status pagamento) m.pagamento/pago)
    {:id (:id-pedido pagamento)
     :status m.pedido/em-preparo}))
