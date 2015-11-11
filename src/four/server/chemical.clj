(ns four.server.chemical
  (:require [clojure.core.async :refer [>! go]]
            [clojure.java.io :as io]
            [four.transit :as t]
            [four.server.ws :as ws :refer [process-msg]]))

(defmethod process-msg :get-elements [[ws-channel [kw msg]]]
  (let [elements-edn-file (io/file (io/resource "public/elements.edn"))
        elements-edn (-> elements-edn-file slurp read-string)
        transit-msg (t/serialize elements-edn)]
    (go (>! ws-channel transit-msg))))
