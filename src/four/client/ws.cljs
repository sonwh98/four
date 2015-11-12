(ns four.client.ws
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! chan]]
            [four.messaging :as m]
            [four.transit :as t]))

(def to-server-queue (chan 10))

(def websocket-channel (atom nil))

(defmulti process-msg (fn [[kw msg]]
                        kw))

(defn init []
  (go (let [host (.. js/window -location -hostname)
            url (str "ws://" host ":9090")
            {:keys [ws-channel error]} (<! (ws-ch url))]
        (reset! websocket-channel ws-channel)
        (m/broadcast [:ws/open true]))))

(defn send! [msg]
  (put! to-server-queue msg))

(defn send-to-server []
  (go-loop []
    (let [msg (<! to-server-queue)]
      (>! @websocket-channel msg))
    (recur)))

(defn listen-for-messages []
  (go-loop []
    (if-let [{:keys  [message]} (<! @websocket-channel)]
      (let [msg (t/deserialize message)]
        (process-msg msg)
        (recur)))))

(def when-ws-open (m/create-topic-fn-handler :ws/open))

(init)
(when-ws-open send-to-server)
(when-ws-open listen-for-messages)
