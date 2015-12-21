(ns four.client.menu
  (:require [four.client.dom :as dom]
            [four.client.ws :as ws :refer [process-msg]]
            [four.messaging :as m]
            [crate.core :as c]
            [reagent.core :as r]))

(defonce catalog (r/atom nil))

(defn category-buttons []
  [:div {:id "category-buttons-container"}
   (for [category @catalog
         :let [cat-name (:category/name category)]]
     [:button {:id cat-name
               :key cat-name} cat-name])])

(defn categories []
  (for [category @catalog
        :let [color (-> (* (rand) 0.5) (+ 0.25))
              products (:products category)
              id (str "category-" (:category/name category))]]
    [:div {:id    id
           :key id
           :class "category"
           :style {:backgroundColor (str "rgb(0,127,127)")
                   :border-style "solid"
                   :border-color "white"
                   }}
     (for [p products]
       [:button {:id (:product/sku p)
                 :key (:product/sku p)
                 :class "product"}
        [:img {:src (or (:url p) "http://www.creattor.com/files/10/652/drinks-icons-screenshots-1.png")
               :class "product-img"}]
        [:div  (:product/name p)]])]))

(defn app []
  [:div
   (category-buttons)
   (categories)])

(defn build-ui []
  (r/render [app] js/document.body)
  )

(defmethod process-msg :catalog [[_ catalog-from-server]]
  (reset! catalog catalog-from-server)
  (build-ui))

(defn send-get-catalog []
  (dom/whenever-dom-ready #(ws/send! [:get-catalog true])))

(defn on-js-reload [])


(send-get-catalog)



