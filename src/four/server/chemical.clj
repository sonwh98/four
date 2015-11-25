(ns four.server.chemical
  (:require [clojure.core.async :refer [>! go]]
            [four.server.db :as db]
            [four.transit :as t]
            [four.server.ws :as ws :refer [process-msg]]))

(defn get-elements []
  (db/q '[:find [(pull ?e  [*]) ...] :where [?e :element/name _]]))

(defmethod process-msg :get-elements [[websocket-channel [kw msg]]]
  (let [elements (get-elements)
        transit-msg (t/serialize [:elements elements])]
    (ws/send! websocket-channel transit-msg)))
