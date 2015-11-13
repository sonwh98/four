(ns four.server.ws
  (:require [datomic.api :as d]
            [clojure.core.async :refer [<! >! put! close! go go-loop]]
            [four.transit :as t]
            [chord.http-kit :refer [with-channel]]
            [environ.core :refer [env]]))

;;websocket-channel is a bidirectional core.async channel to read from and write messages to clients via websocket
;;websocket-channels contains all active websocket-channel. If you need to send messages to all clients
;;you can iterate over websocket-channels and use core.async/put! to send messages to clients
(def websocket-channels (atom []))

(defmulti process-msg (fn [[websocket-channel [kw msg]]]
                        kw))

(defn remove-channel [websocket-channel]
  (reset! websocket-channels (filter #(not= % websocket-channel) @websocket-channels)))

(defn clean-up! [websocket-channel]
  (println "clean-up " websocket-channel)
  (remove-channel websocket-channel)
  (close! websocket-channel))

(defn send! [websocket-channel transit-msg]
  (go (>! websocket-channel transit-msg)))

(defn listen-for-messages-on [websocket-channel]
  (go-loop []
    (if-let [{:keys  [message]} (<! websocket-channel)]
      (do
        (process-msg [websocket-channel message])
        (recur))
      (clean-up! websocket-channel))))

(defn websocket-handler [request]
  (with-channel request websocket-channel
    (swap! websocket-channels conj websocket-channel)
    (listen-for-messages-on websocket-channel)))
