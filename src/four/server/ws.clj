(ns four.server.ws
  (:require [datomic.api :as d]
            [clojure.core.async :refer [<! >! put! close! go go-loop]]
            [four.transit :as t]
            [chord.http-kit :refer [with-channel]]
            [environ.core :refer [env]]))

(def client-channels (atom []))

(defmulti process-msg (fn [[ws-channel [kw msg]]]
                        kw))
(defn clean-up! [ws-channel]
  (println "clean-up " ws-channel)
  (reset! client-channels (filter #(not= % ws-channel) @client-channels))
  (close! ws-channel))

(defn listen-for-messages-on [ws-channel]
  (go-loop []
    (if-let [{:keys  [message]} (<! ws-channel)]
      (do
        (swap! client-channels conj ws-channel)
        (process-msg [ws-channel message])
        (recur))
      (clean-up! ws-channel))))

(defn websocket-handler [request]
  (with-channel request ws-channel
                (listen-for-messages-on ws-channel)))