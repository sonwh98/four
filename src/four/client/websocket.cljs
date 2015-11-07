(ns four.client.websocket
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! <! >! chan]]
            [four.transit :as transit]
            [four.messaging :as m]))


(defonce to-server-message-queue (chan 10))

(defn- to-server [websocket]
  (go (while true
        (let [msg (<! to-server-message-queue)
              transit-msg (transit/serialize msg)]
          (println "sending " transit-msg)
          (. websocket send transit-msg)))))

(let [host (.. js/window -location -hostname)
      websocket (js/WebSocket. (str "ws://" host ":9090"))]
  (set! (. websocket -onopen) (fn [evt]
                         (to-server websocket)))
  (set! (. websocket -onclose) (fn [evt]
                          (println "onclose " evt)))
  (set! (. websocket -onmessage) (fn [evt]
                            (let [transit-msg (. evt -data)
                                  msg (transit/deserialize transit-msg)]
                              (m/broadcast msg))))
  
  (set! (. websocket -onerror) (fn [evt]
                          (println "onerror " evt))))


(defn send! [msg]
  (put! to-server-message-queue msg))
