(ns mba-fiap.lanchonete-test
  (:require [clj-http.client :as clj-http]
            [clojure.data.json :as json]
            [clojure.test :refer :all]
            [hato.client :as hc]
            [malli.generator :as mg]
            [mba-fiap.model.cliente :as cliente]
            [mba-fiap.model.pedido :as pedido]
            [mba-fiap.model.produto :as produto]
            [mba-fiap.system :as system]))

(defn postgre-fixture [f]
  (let [pg-container (system/start-pg-container)]

    (try
      (f)
      (catch Exception e
        (prn e))
      (finally (system/stop-pg-container)))))

(defn system-fixture [f]
  (let [_system (system/system-start)]
    (try
      (f)
      (catch Exception e
        (prn e))
      (finally (system/system-stop)))))


(use-fixtures :once (join-fixtures [postgre-fixture system-fixture]))


(deftest test-main
  (testing "main startup ok"
    (let [{:keys [body status]} (hc/get "http://localhost:8080/produtos/lanche")]
      (is (= 200 status))
      (is (= "[{\"id\":\"0af17083-4d2b-44d0-86cd-218ba2ba1c55\",\"nome\":\"X-bacon\",\"descricao\":\"p\\u00e3o com porco\",\"preco_centavos\":1500,\"categoria\":\"lanche\"}]"
             body)))))


(deftest cliente-crud
  (let [by-cpf (mg/generate cliente/CPFIdentifiedCliente)
        by-email (mg/generate cliente/EmailIdentifiedCliente)
        anonymous (mg/generate cliente/AnonymousCliente)]
    (testing "Insert"
      (let [[cpf email anon] (->> [by-cpf by-email anonymous]
                                  (map #(hash-map :headers {"content-type" "application/json"}
                                                  :body (json/write-str %)

                                                  :throw-exceptions false))
                                  (map #(hc/post "http://localhost:8080/cliente" %))
                                  (mapv #(update % :body json/read-str))
                                  doall)]

        (is (= 200 (:status cpf)))
        (is (= 200 (:status email)))
        (is (= 200 (:status anon)))))

    (testing "Query by CPF"
      (let [{:keys [body] :as result} (hc/get (str "http://localhost:8080/cliente/" (:cpf by-cpf)))
            body (json/read-str body :key-fn keyword)]
        (is (= 200 (:status result)))
        (is (= (:cpf by-cpf) (:cpf body)))))

    (testing "Autenticar cliente - ok"
      (with-redefs [clj-http/post (constantly
                                    {:status 200
                                     :body   (json/write-str
                                               {:AuthenticationResult
                                                {:AccessToken "xarlinho"}})})]
        (let [{:keys [body] :as result} (hc/post "http://localhost:8080/autenticar"
                                                 {:headers          {"content-type" "application/json"}
                                                  :throw-exceptions false
                                                  :body             (json/write-str {:cpf      (:cpf by-cpf)
                                                                                     :email    "tchubironson@email.com"
                                                                                     :password "password"})})
              body (json/read-str body :key-fn keyword)]
          (is (= 200 (:status result)))
          (is (contains? body :email))
          (is (contains? body :token)))))
    (testing "Autenticar cliente - not ok"
      (with-redefs [clj-http/post (constantly
                                    {:status 404
                                     :body   (json/write-str {})})]
        (let [{:keys [body] :as result} (hc/post "http://localhost:8080/autenticar"
                                                 {:headers          {"content-type" "application/json"}
                                                  :throw-exceptions false
                                                  :body             (json/write-str {:cpf      (:cpf by-cpf)
                                                                                     :email    "tchubironson25@email.com"
                                                                                     :password "password"})})
              body (json/read-str body :key-fn keyword)]
          (is (= 400 (:status result)))
          (is (= {:error "The client does not exists in our system"} body)))))))

(deftest produto-tests
  (let [new-product (mg/generate produto/Produto {:size 35})
        cleanup (fn [p] (-> (dissoc p :descricao :id :preco_centavos)
                            (assoc :preco-centavos (:preco_centavos p (:preco-centavos p)))))]
    (testing "criar-produto"
      (let [response (hc/post "http://localhost:8080/produto"
                              {:headers          {"content-type" "application/json"}
                               :body             (json/write-str new-product)
                               :throw-exceptions false})
            body (json/read-str (:body response) :key-fn keyword)]
        (is (= 200 (:status response)))
        (is (= (cleanup new-product) (cleanup body)))))

    (testing "listar-produtos"
      (let [response (hc/get (str "http://localhost:8080/produtos/" (:categoria new-product))
                             {:throw-exceptions false})
            body (->> (json/read-str (:body response) :key-fn keyword)
                      (group-by :nome))]
        (is (= 200 (:status response)))
        (is (= (cleanup new-product) (cleanup (first (get body (:nome new-product))))))))

    (testing "editar-produto"
      (let [updated-product (assoc new-product :descricao "Updated Name")
            response (hc/get (str "http://localhost:8080/produtos/" (:categoria new-product))
                             {:headers          {"content-type" "application/json"}
                              :throw-exceptions false})
            [{:keys [id]}] (json/read-str (:body response) :key-fn keyword)
            response (hc/put (str "http://localhost:8080/produto/" id)
                             {:headers          {"content-type" "application/json"}
                              :body             (json/write-str updated-product)
                              :throw-exceptions false})
            body (-> (json/read-str (:body response) :key-fn keyword)
                     (dissoc :id))]
        (is (= 200 (:status response)))))


    (testing "deletar-produto"
      (let [response (hc/get (str "http://localhost:8080/produtos/" (:categoria new-product))
                             {:headers          {"content-type" "application/json"}
                              :throw-exceptions false})
            [{:keys [id]}] (json/read-str (:body response) :key-fn keyword)
            response (hc/delete (str "http://localhost:8080/produto/" (str id))
                                {:throw-exceptions false})]
        (is (= 200 (:status response)))))))

(defn setup-pedido []
  (let [by-cpf (mg/generate cliente/CPFIdentifiedCliente)
        by-cpf (-> (hc/post "http://localhost:8080/cliente" {:headers          {"content-type" "application/json"}
                                                             :body             (json/write-str by-cpf)
                                                             :throw-exceptions false})
                   :body
                   (json/read-str :key-fn keyword))
        lanche (assoc (mg/generate produto/Produto) :categoria "lanche")
        bebida (assoc (mg/generate produto/Produto) :categoria "bebida")
        acompanhamento (assoc (mg/generate produto/Produto) :categoria "acompanhamento")
        [lanche bebida acompanhamento] (->> [lanche bebida acompanhamento]
                                            (map #(hc/post "http://localhost:8080/produto"
                                                           {:headers          {"content-type" "application/json"}
                                                            :body             (json/write-str %)
                                                            :throw-exceptions false}))
                                            (map (comp #(json/read-str % :key-fn keyword) :body)))]
    (-> (mg/generate pedido/Pedido)
        (assoc :total (reduce + (map :preco-centavos [lanche bebida acompanhamento])))
        (assoc :produtos [(parse-uuid (:id lanche))
                          (parse-uuid (:id bebida))
                          (parse-uuid (:id acompanhamento))]
               :id-cliente (:id by-cpf)))))

(deftest pedido-tests
  (testing "cadastrar-pedido"
    (let [new-pedido (setup-pedido)
          response (hc/post "http://localhost:8080/pedido"
                            {:headers          {"content-type" "application/json"}
                             :body             (json/write-str new-pedido)
                             :throw-exceptions false})]
      (is (= 200 (:status response)))
      ))

  (testing "listar-pedidos"
    (let [response (hc/get "http://localhost:8080/pedidos"
                           {:throw-exceptions false})
          body (json/read-str (:body response) :key-fn keyword)]
      (is (= 200 (:status response)))
      (is (= 1 (count body)))))

  (testing "editar-pedido"
    (let [new-pedido (setup-pedido)
          response (hc/post "http://localhost:8080/pedido"
                            {:headers          {"content-type" "application/json"}
                             :body             (json/write-str new-pedido)
                             :throw-exceptions false})
          pedido (json/read-str (:body response) :key-fn keyword)
          updated-pedido (-> new-pedido
                             (assoc :status "finalizado")
                             (dissoc :created-at))
          response (hc/put (str "http://localhost:8080/pedido/" (:id pedido))
                           {:headers          {"content-type" "application/json"}
                            :body             (json/write-str updated-pedido)
                            :throw-exceptions false})
          body (json/read-str (:body response) :key-fn keyword)]
      (is (= 200 (:status response)))
      (is (= "finalizado" (:status body))))))