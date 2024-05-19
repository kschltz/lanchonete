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
    (try
      (run! #(.closeDispatcher connection %) dispatchers)
      (.close connection)
      (catch Exception e
        (println ::27 e))))

  INATSClient
  (subscribe [_this subject handler]
    (let [dispatcher (reify MessageHandler
                       (^void onMessage [_ ^Message msg]
                         (handler msg)))]
      (-> (.createDispatcher connection dispatcher)
          (.subscribe subject))))

  (publish [_ subject msg]
    (let [reply-to (str app-name "." subject ".reply")]
      (try
        (.publish connection
                  subject
                  reply-to
                  (.getBytes (str msg) StandardCharsets/UTF_8))
        (catch Exception e
          (println ::44 "FAILED TO PUBLISH MESSAGE" e)
          (throw e))))))

(defrecord MemoryNats [store]
  Closeable
  (close [_]
    (println "Closing MemoryNats")
    (reset! store {}))
  INATSClient
  (subscribe [_ subject handler]
    (let [dispatcher (reify MessageHandler
                       (^void onMessage [_ ^Message msg]
                         (handler msg)))]
      (run! #(.onMessage dispatcher %) (get @store subject))))

    (publish [_ subject msg]
             (swap! store update subject conj msg)))

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

(defmethod ig/init-key ::memory-nats [_ _]
  (->MemoryNats (atom {})))

(defmethod ig/halt-key! ::memory-nats [_ memory-nats]
  (.close memory-nats))

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













