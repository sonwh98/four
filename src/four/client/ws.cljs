(ns four.client.ws
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! chan]]
            [four.messaging :as m]
            [four.transit :as t]))

;;server-websocket-channel contains a bidirectional core.async channel used to send and read messages from the server
(def server-websocket-channel (atom nil))

;;function that executes a call-back-fn if the :websocket/open topic has ever been broadcasted
;;example usage: (if-websocket-open #(println "websocket is open"))
(def whenever-websocket-open (m/whenever :websocket/open :broadcasted))

;;multi method that dispatches based on the first attribute of the msg. a msg is a vector of the form [keyword val]
(defmulti process-msg (fn [[kw val]]
                        kw))

(defn connect-to-websocket-server []
  (go (let [host (.. js/window -location -hostname)
            url (str "ws://" host ":9090")
            {:keys [ws-channel error]} (<! (ws-ch url))]
        (reset! server-websocket-channel ws-channel)
        (m/broadcast [:websocket/open true]))))

(defn send! [msg]
  (whenever-websocket-open #(go (>! @server-websocket-channel msg))))

(defn listen-for-messages-from-websocket-server []
  (go-loop []
    (if-let [{:keys  [message]} (<! @server-websocket-channel)]
      (let [msg (t/deserialize message)]
        (process-msg msg)
        (recur)))))

(connect-to-websocket-server)
(whenever-websocket-open listen-for-messages-from-websocket-server)
