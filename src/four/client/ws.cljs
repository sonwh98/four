(ns four.client.ws
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! chan]]
            [four.messaging :as m]
            [four.transit :as t]))

;websocket-channel contains a bidirectional core.async channel used to send and read messages from the server
(def websocket-channel (atom nil))
(def when-ws-open (m/create-topic-fn-handler :ws/open))

(defmulti process-msg (fn [[kw msg]]
                        kw))

(defn init []
  (go (let [host (.. js/window -location -hostname)
            url (str "ws://" host ":9090")
            {:keys [ws-channel error]} (<! (ws-ch url))]
        (reset! websocket-channel ws-channel)
        (m/broadcast [:ws/open true]))))

(defn send! [msg]
  (when-ws-open #(go (>! @websocket-channel msg))))

(defn listen-for-messages []
  (go-loop []
    (if-let [{:keys  [message]} (<! @websocket-channel)]
      (let [msg (t/deserialize message)]
        (process-msg msg)
        (recur)))))



(init)
(when-ws-open listen-for-messages)
