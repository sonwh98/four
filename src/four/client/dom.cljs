(ns four.client.dom
  (:require [four.messaging :as m]
            [cljs.core.async :refer [put! chan]]))

(.. js/document (addEventListener "DOMContentLoaded" (fn []
                                                       (m/broadcast [:dom/content-loaded true]))))

(defn by-id [id]
  (.. js/document (getElementById id)))


(defn event->chan [element event-str]
  (let [event-chan (chan 1)]
    (.. element (addEventListener event-str (fn [event]
                                              (put! event-chan event))))
    event-chan))

(defn on [element event-str call-back-fn]
  (.. element (addEventListener event-str call-back-fn)))

(defn str->dom-element [html-str]
  (let [div (.. js/document (createElement "div"))]
    (set! (.. div -innerHTML) html-str)
    (.. div -firstChild)))