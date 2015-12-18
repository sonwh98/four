(ns four.client.menu
  (:require [four.client.core :as four]
            [four.client.dom :as dom]
            [four.client.ws :as ws :refer [process-msg]]
            [four.messaging :as m]
            [crate.core :as c]
            [rum.core :as rum]))

(rum/defc category-selection-container [catalog]
  [:div {:id "category-selection-container"}
   (for [category catalog
         :let [cat-name (:category/name category)]]
     [:button {:id cat-name
               :key cat-name} cat-name])])



(defn build-scene [catalog]
  (four/init)
  (let [categories (category-selection-container catalog)
        categories2 (category-selection-container catalog)]
    (rum/mount categories (dom/by-id "root"))

    )

  (let [active-category-button (atom nil)
        active-category-container (atom nil)
        category-button-container-css3d-object (four/element->css3d-object (dom/by-id "category-selection-container"))
        category-buttons (array-seq (.. category-button-container-css3d-object -element (querySelectorAll "button")))
        categories  (for [category catalog
                          :let [color (-> (* (rand) 0.5) (+ 0.25))
                                products (:products category)
                                id (str "category-" (:category/name category))

                                div [:div {:id    id
                                           :class "category"
                                           :style {:backgroundColor (str "rgb(0,127,127)")
                                                   :border-style "solid"
                                                   :border-color "white"
                                                   }}
                                     (for [p products]
                                       [:button {:id (:product/sku p)
                                                 :class "product"}
                                        [:img {:src (or (:url p) "http://www.creattor.com/files/10/652/drinks-icons-screenshots-1.png")
                                               :class "product-img"}]
                                        [:div  (:product/name p)]])]
                                css3d-object (four/hiccup->css3d-object div)]]
                      (do
                        (four/add css3d-object)
                        css3d-object))
        get-category-container (fn [category-button]
                                 (let [category-name (. category-button -id)
                                       id (str "category-" category-name)]
                                   (four/id->object3d id)))

        set-active-category-container (fn [category-button]
                                        (let [category-container-css3dobj (get-category-container category-button)
                                              x-far-left (:x (four/get-top-left))
                                              slide-out (fn [active-container]
                                                          (let [category-container-div (. active-container -element)
                                                                div-width (. category-container-div -clientWidth)]
                                                            (four/morph [category-container-css3dobj] :into [{:x (+ x-far-left
                                                                                                                    (/ div-width 2)
                                                                                                                    10)
                                                                                                              :y -40
                                                                                                              :z 0}])))]
                                          (if (nil? @active-category-button)
                                            (do
                                              (reset! active-category-button category-button)
                                              (reset! active-category-container category-container-css3dobj)))
                                          
                                          (set! (.. @active-category-button -style -backgroundColor) nil)
                                          (four/move-off-screen [@active-category-container])

                                          (reset! active-category-button category-button)
                                          (reset! active-category-container category-container-css3dobj)
                                          (set! (.. @active-category-button -style -backgroundColor) "rgb(100,100,100)")
                                          
                                          (slide-out @active-category-container)))]
    
    (four/add category-button-container-css3d-object)
    
    (doseq [category categories]
      (four/move-off-screen [category]))
    
    (doseq [category-button category-buttons]
      (dom/on category-button "click" #(set-active-category-container category-button)))

    (m/postpone (fn []
                  (set-active-category-container (first category-buttons))
                  (let [category-button-container-div (dom/by-id "category-selection-container")
                        top-left (update-in (four/get-top-left) [:x] #(+ %
                                                                         (/ (. category-button-container-div -clientWidth)
                                                                            2)
                                                                         10))
                        top-left (update-in top-left [:y] #(+ %
                                                              (/ (. category-button-container-div -clientHeight)
                                                                 -2)
                                                              -10
                                                              ))]
                    (four/morph [category-button-container-css3d-object]
                                :into
                                [top-left])))
                1000)
    
    )
  )

(defmethod process-msg :catalog [[_ catalog]]
  (build-scene catalog))

(defn send-get-catalog []
  (dom/whenever-dom-ready #(ws/send! [:get-catalog true])))

(defn on-js-reload [])

(send-get-catalog)



