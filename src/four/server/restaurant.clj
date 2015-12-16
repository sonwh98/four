(ns four.server.restaurant
  (:require [clojure.core.async :refer [>! go]]
            [four.server.db :as db]
            [four.transit :as t]
            [four.server.ws :as ws :refer [process-msg]]))

(defn get-catalog []
  (db/q '[:find [(pull ?c  [:category/name {:products [:product/sku :product/name :product/price :url] }]) ...] :where [_ :catalog ?c]]))

(defmethod process-msg :get-catalog [[websocket-channel [kw msg]]]
  (let [catalog (get-catalog)
        transit-msg (t/serialize [:catalog catalog])]
    (println catalog)
    (ws/send! websocket-channel transit-msg)))
