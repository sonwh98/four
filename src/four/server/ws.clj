(ns four.server.ws
  (:require [datomic.api :as d]
            [clojure.core.async :refer [<! >! put! close! go go-loop]]
            [four.transit :as t]
            [chord.http-kit :refer [with-channel]]
            [environ.core :refer [env]]))

;;a client-websocket-channel is a bidirectional core.async channel to read from and write messages to clients via websocket
;;client-websocket-channels contains all active/opened client-websocket-channel. If you need to send messages to all clients
;;you can iterate over client-websocket-channels and use core.async/put! to send messages to clients
(def client-websocket-channels (atom []))

(defmulti process-msg (fn [[client-websocket-channel [kw msg]]]
                        kw))

(defn send! [client-websocket-channel transit-msg]
  (go (>! client-websocket-channel transit-msg)))

(defn- remove-channel [client-websocket-channel]
  (reset! client-websocket-channels (filter #(not= % client-websocket-channel) @client-websocket-channels)))

(defn- clean-up! [client-websocket-channel]
  (println "clean-up " client-websocket-channel)
  (remove-channel client-websocket-channel)
  (close! client-websocket-channel))

(defn- listen-for-messages-on [client-websocket-channel]
  (go-loop []
    (if-let [{:keys  [message]} (<! client-websocket-channel)]
      (do
        (process-msg [client-websocket-channel message])
        (recur))
      (clean-up! client-websocket-channel))))

(defn websocket-handler [request]
  (with-channel request websocket-channel
    (swap! client-websocket-channels conj websocket-channel)
    (listen-for-messages-on websocket-channel)))
