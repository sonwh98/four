(ns four.client.ws
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! chan]]
            [four.messaging :as m]))

(def to-server-queue (chan 10))
(def from-server-queue (chan 10))

(def websocket-channel (atom nil))

(defn init []
  (go (let [host (.. js/window -location -hostname)
            url (str "ws://" host ":9090")
            {:keys [ws-channel error]} (<! (ws-ch url))]
        (reset! websocket-channel ws-channel)
        (m/broadcast [:ws/open true]))))

(defn send! [msg]
  (put! to-server-queue msg)
  from-server-queue)

(defn send-to-server []
  (go-loop []
    (let [msg (<! to-server-queue)]
      (>! @websocket-channel msg))
    (recur)))

(defn read-from-server []
  (go-loop []
    (let [data-from-server (<! @websocket-channel)]
      (put! from-server-queue (:message data-from-server))
      (recur))))

(def when-ws-open (m/create-topic-fn-handler :ws/open))

(init)
(when-ws-open send-to-server)
(when-ws-open read-from-server)
