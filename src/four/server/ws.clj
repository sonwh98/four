(ns four.server.ws
  (:require [datomic.api :as d]
            [clojure.core.async :refer [<! >! put! close! go go-loop]]
            [four.transit :as t]
            [chord.http-kit :refer [with-channel]]
            [environ.core :refer [env]]))

(def client-channels (atom []))

(defn clean-up! [ws-channel]
  (println "clean-up " ws-channel)
  (reset! client-channels (filter #(not= % ws-channel) @client-channels))
  (close! ws-channel))

(defmulti process-msg (fn [[ws-channel [kw msg]]]
                        kw))
(defn process-messages! [ws-channel]
  (go-loop []
    (if-let [{:keys  [message]} (<! ws-channel)]
      (do
        (swap! client-channels conj ws-channel)
        (process-msg [ws-channel message])
        (recur))
      (clean-up! ws-channel))))

(defn websocket-handler [request]
  (with-channel request ws-channel
                (process-messages! ws-channel)))