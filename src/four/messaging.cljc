(ns four.messaging
  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go go-loop]]))
  #?(:cljs (:require [cljs.core.async :as async :refer [<! >! ]]))
  #?(:clj  (:require [clojure.core.async :as async :refer [<! >! go go-loop]])))


;;a message is a vector of the form [topic value]
;;the topic can be any value but should be a keyword
(defonce message-bus (async/chan 10))
(defonce message-publication (async/pub message-bus (fn [msg]
                                                (if (vector? msg)
                                                  (first msg)
                                                  :no-topic))))
(defn broadcast [msg]
  ;(println "boradcast " (first msg))
  (async/put! message-bus msg))

(defn subscribe-to
  [topic]
  (let [channel (async/chan 10)]
    (async/sub message-publication topic channel)
    channel))

(defn unsubscribe
  [channel topic]
  (async/unsub message-publication topic channel))

(defn on
  [topic call-back-fn]
  (let [topic-chan (subscribe-to topic)]
    (go (while true
          (call-back-fn (<! topic-chan))))))

(defn whenever
  "returns a closure that takes a call-back-fn which is executed when ever the topic message been broadcasted"
  [topic _ ]
  (let [topic-message-recieved? (atom false)]
    (on topic #(reset! topic-message-recieved? true))
    (fn [call-back-fn]
      (if @topic-message-recieved?
        (call-back-fn)
        (on topic #(do
                     (reset! topic-message-recieved? true)
                     (call-back-fn)))))))

(defn postpone [execute-fn ms]
  (go (<! (async/timeout ms))
      (execute-fn)))
