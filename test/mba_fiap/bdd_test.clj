(ns mba-fiap.bdd-test
  (:require [clojure.data.json :as json]
            [clojure.test :refer :all]
            [malli.generator :as mg]
            [mba-fiap.system :as system]
            [hato.client :as hc]
            [mba-fiap.value-object.cpf :as cpf]
            [fundingcircle.jukebox.alias.cucumber :as cucumber]))

;;BDDs
(defn i-have-a-new-client
  "Sets up a new client with CPF and email."
  {:scene/step "I have a new client with CPF"}
  [x]
  (system/start-pg-container)
  (system/system-start)
  (Thread/sleep 4000)
  {:cpf (mg/generate cpf/CPF)})

(defn i-insert-the-new-client
  "Inserts the new client."
  {:scene/step "I insert the new client"}
  [{:keys [cpf]}]

  (let [response (hc/post "http://localhost:8080/cliente"
                          {:body (json/write-str {:cpf cpf})
                           :headers {"Content-Type" "application/json"}})]
    (if (= 200 (:status response))
      {:cpf cpf}
      (throw (Exception. "Failed to insert the new client")))))

(defn i-should-receive-a-200-status-code
  "Checks if the client was inserted successfully."
  {:scene/step "I should receive a 200 status code"}
  [client]
  (let [response (hc/get (str "http://localhost:8080/cliente/" (:cpf client)))]
    (if (= 200 (:status response))
      client
      (throw (Exception. "Failed to retrieve the inserted client")))
    (system/system-stop)
    (system/stop-pg-container)))

(defn run-cucumber []
  (cucumber/-main "-g" "./test/mba_fiap/" "./test/resources/"))
