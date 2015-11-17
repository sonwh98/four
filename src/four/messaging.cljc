(ns four.messaging
  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go go-loop]]))
  #?(:cljs (:require [cljs.core.async :refer [put! <! >! chan pub sub unsub]]))
  #?(:clj  (:require [clojure.core.async :refer [put! <! >! chan pub sub unsub go go-loop]])))

#?(:cljs (enable-console-print!))

;;a message is of a vector of the form [topic value]
;;the topic can be value but should be a keyword
(defonce message-bus (chan 10))
(defonce message-publication (pub message-bus (fn [msg]
                                                (if (vector? msg)
                                                  (first msg)
                                                  :no-topic))))
(defn broadcast [msg]
  (println "boradcast " (first msg))
  (put! message-bus msg))

(defn subscribe-to
  [topic]
  (let [channel (chan 10)]
    (sub message-publication topic channel)
    channel))

(defn unsubscribe
  [channel topic]
  (unsub message-publication topic channel))

(defn on
  [topic call-back-fn]
  (let [topic-chan (subscribe-to topic)]
    (go (while true
          (call-back-fn (<! topic-chan))))))

(defn when-ever
  "returns a function that takes a call-back-fn which is executed when ever the topic message been broadcasted"
  [topic _ ]
  (let [topic-message-recieved? (atom false)]
    (on topic #(reset! topic-message-recieved? true))
    (fn [call-back-fn]
      (if @topic-message-recieved?
        (call-back-fn)
        (on topic #(do
                     (reset! topic-message-recieved? true)
                     (call-back-fn)))))))
