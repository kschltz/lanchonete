(ns mba-fiap.service.auth
  (:require
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [clojure.tools.logging :as log]))

(defn external-auth [api-url email password]
  (log/info "Sending authentication request" email)
  (try
    (let [response (client/post api-url
                                {:content-type :json
                                 :body (json/write-str {:email email
                                                        :password password})})
          parsed-response (json/read-str (:body response) :key-fn keyword)
          auth-result (:AuthenticationResult parsed-response)
          access-token (:AccessToken auth-result)]
      (if (and (= 200 (:status response)) auth-result access-token)
        {:token access-token}
        (do
          (log/error "Authentication failed or unexpected response structure" parsed-response)
          {:status :error :message "Authentication failed or unexpected response structure"})))
    (catch Exception e
      (log/error e "Failed to authenticate" email)
      {:status :error :message "Failed to authenticate"})))

; Needs to set this as a environment variable
(def url "https://clylws7uvb.execute-api.us-east-1.amazonaws.com/v1/authenticate")

(defn auth [email password]
  (let
   [data (external-auth url email password)]
    (:token data)))

