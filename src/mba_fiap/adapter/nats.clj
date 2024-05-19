(ns mba-fiap.adapter.nats
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig])
  (:import (io.nats.client Connection$Status Message MessageHandler Nats Options)
           (java.io Closeable)
           (java.nio.charset StandardCharsets)))



(defprotocol INATSClient
  (subscribe [this subject handler])
  (publish [_ subject msg]))

(defn ->dispatcher [f]
  (reify MessageHandler
    (^void onMessage [_ ^Message msg]
      (f msg))))

(defrecord NATSClient [app-name connection dispatchers]
  Closeable
  (close [_]
    (run! #(.closeDispatcher connection %) dispatchers)
    (.close connection))

  INATSClient
  (subscribe [_this subject handler]
    (let [dispatcher (reify MessageHandler
                       (^void onMessage [_ ^Message msg]
                         (handler msg)))]
      (-> (.createDispatcher connection dispatcher)
          (.subscribe subject))))

  (publish [_ subject msg]
    (let [reply-to (str app-name "." subject ".reply")]
      (.publish connection
                subject
                reply-to
                (.getBytes (str msg) StandardCharsets/UTF_8)))))

(defn nats-client [{:keys [app-name url subjects-handlers]}]
  (let [connection (Nats/connect (-> (Options/builder)
                                     (.server url)
                                     (.build)))
        dispatchers (->> subjects-handlers
                         (mapv (fn [[subject handler]]
                                 (doto (.createDispatcher connection (->dispatcher handler))
                                   (.subscribe subject)))))]
    (loop [status (.getStatus connection)]
      (prn "NATS connection status: " status)
      (if (= status Connection$Status/CONNECTED)
        connection
        (recur (.getStatus connection))))

    (->NATSClient app-name connection dispatchers)))

(defmethod ig/init-key ::client [_ {:keys [url app-name subjects-handlers]}]
  (let [client (nats-client {:url               url
                             :app-name          app-name
                             :subjects-handlers subjects-handlers})]
    client))

(defmethod ig/halt-key! ::client [_ client]
  (.close client))

(defmethod ig/init-key ::simple-handler [_ {:keys [ctx handler-fn]}]
  (partial (eval handler-fn) ctx))


(comment

  (def control (atom true))

  (def c1 (nats-client {:url               "nats://66.51.121.86:4222"
                        :app-name          "lanchonete"
                        :subjects-handlers {"lanchonete.*" #(prn "1" (.getSubject %) " " (String. (.getData %)))}
                        }))
  (def c2 (nats-client {:url               "nats://66.51.121.86:4222"
                        :app-name          "pagamento"
                        :subjects-handlers {"pagamento.*" #(prn "2" (.getSubject %) " " (String. (.getData %)))}
                        }))

  (future (with-open [c (nats-client {:url               "nats://66.51.121.86:4222"
                                      :app-name          "lanchonete"
                                      :subjects-handlers {}
                                      ;{"lanchonete.*" #(prn (.getSubject %) " " (String. (.getData %)))}
                                      })]

            (loop [ctrl @control]
              (if (not ctrl)
                :done
                (do
                  (Thread/sleep 1000)
                  (prn "sending")
                  (publish c "novos-pedidos" "YAMETE KUDASAI")
                  (recur @control))))))
  )













