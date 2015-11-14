(ns four.messaging
  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go go-loop]]))
  #?(:cljs (:require [cljs.core.async :refer [put! <! >! chan pub sub unsub]]))
  #?(:clj  (:require [clojure.core.async :refer [put! <! >! chan pub sub unsub go go-loop]])))

#?(:cljs (enable-console-print!))

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

(defn if-ever
  [topic _ ]
  (let [topic-recieved? (atom false)]
    (on topic #(reset! topic-recieved? true))
    (fn [call-back-fn]
      (if @topic-recieved?
        (call-back-fn)
        (on topic #(do
                     (reset! topic-recieved? true)
                     (call-back-fn)))))))
