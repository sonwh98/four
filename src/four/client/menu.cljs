(ns four.client.menu
  (:require [four.client.core :as four :refer [morph div->css3d-object]]
            [four.client.dom :as dom]
            [four.client.layout :as layout]
            [four.client.ws :as ws :refer [process-msg]]
            [four.messaging :as m]
            [crate.core :as c]))


(defn build-scene [catalog]
  (doseq [p catalog]
    (println (:products  p)))
  )

(defmethod process-msg :catalog [[_ catalog]]
  (build-scene catalog))

(defn send-get-catalog []
  (println "send-get-catalog")
  (dom/whenever-dom-ready #(ws/send! [:get-catalog true])))

(defn on-js-reload [])

(send-get-catalog)
