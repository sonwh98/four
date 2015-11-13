(ns four.server.chemical
  (:require [clojure.core.async :refer [>! go]]
            [clojure.java.io :as io]
            [four.transit :as t]
            [four.server.ws :as ws :refer [process-msg]]))

(defn get-elements []
  (let [elements-edn-file (io/file (io/resource "public/elements.edn"))
        elements-edn [:elements (-> elements-edn-file slurp read-string)]
        transit-msg (t/serialize elements-edn)]
    transit-msg))

(defmethod process-msg :get-elements [[websocket-channel [kw msg]]]
  (let [transit-msg (get-elements)]
    (ws/send! websocket-channel transit-msg)))
