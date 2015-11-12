(ns four.server.ws
  (:require [datomic.api :as d]
            [clojure.core.async :refer [<! >! put! close! go go-loop]]
            [four.transit :as t]
            [chord.http-kit :refer [with-channel]]
            [environ.core :refer [env]]))

;websocket-channel is a bidirectional core.async channel to read from and write messages to clients via websocket
;websocket-channels contains all active websocket-channel. If you need to send messages to all clients
;you can iterate over websocket-channels and use core.async/put! to send messages to clients
(def websocket-channels (atom []))

(defmulti process-msg (fn [[websocket-channel [kw msg]]]
                        kw))

(defn remove-channel [ws-channel]
  (reset! websocket-channels (filter #(not= % ws-channel) @websocket-channels)))

(defn clean-up! [ws-channel]
  (println "clean-up " ws-channel)
  (remove-channel ws-channel)
  (close! ws-channel))

(defn listen-for-messages-on [ws-channel]
  (go-loop []
    (if-let [{:keys  [message]} (<! ws-channel)]
      (do
        (swap! websocket-channels conj ws-channel)
        (println "message=" message)
        (process-msg [ws-channel message])
        (recur))
      (clean-up! ws-channel))))

(defn websocket-handler [request]
  (with-channel request ws-channel
                (listen-for-messages-on ws-channel)))
