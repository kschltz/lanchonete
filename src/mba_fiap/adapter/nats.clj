(ns mba-fiap.adapter.nats
  (:require [clojure.tools.logging :as log])
  (:import (io.nats.client Connection$Status Message MessageHandler Nats Options)
           (java.io Closeable)
           (java.nio.charset StandardCharsets)))



(defprotocol INATSClient
  (publish [_ subject msg]))

(defrecord NATSClient [app-name connection dispatchers]
  Closeable
  (close [_]
    (run! #(.closeDispatcher connection %) dispatchers)
    (.close connection))

  INATSClient
  (publish [_ subject msg]
    (let [subject (str app-name "." subject)
          reply-to (str subject ".reply")]
      (.publish connection
                subject
                reply-to
                (.getBytes msg StandardCharsets/UTF_8)))))

(defn nats-client [{:keys [app-name url subjects-handlers]}]
  (let [connection (Nats/connect (-> (Options/builder)
                                     (.server url)
                                     (.build)))
        ->dispatcher (fn [f] (reify MessageHandler
                               (^void onMessage [_ ^Message msg]
                                 (f msg))))
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

(comment

  (def control (atom true))

  (def c1 (nats-client {:url               "nats://66.51.121.86:4222"
                        :app-name          "lanchonete"
                        :subjects-handlers {"lanchonete.*" #(prn "1" (.getSubject %) " " (String. (.getData %)))}
                        }))
  (def c2 (nats-client {:url               "nats://66.51.121.86:4222"
                        :app-name          "lanchonete"
                        :subjects-handlers {"lanchonete.*" #(prn "2" (.getSubject %) " " (String. (.getData %)))}
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













