(ns four.client.menu
  (:require [four.client.core :as four :refer [morph div->css3d-object]]
            [four.client.dom :as dom]
            [four.client.ws :as ws :refer [process-msg]]
            [four.messaging :as m]
            [crate.core :as c]))

(defn build-scene [catalog]
  (four/init)
  (let [active-category-button (atom nil)
        active-category-container (atom nil)
        category-button-container-template [:div {:id "category-selection-container"}
                                            (for [category catalog
                                                  :let [cat-name (:category/name category)]]
                                              [:button {:id cat-name} cat-name])]
        category-button-container-div (c/html category-button-container-template)
        category-button-container-css3d-object (div->css3d-object category-button-container-div)
        category-buttons (array-seq (.. category-button-container-css3d-object -element (querySelectorAll "button")))
        off-screen-left [{:x (- js/window.innerWidth) :y 0 :z 0}]
        x-far-left (/ js/window.innerWidth -2)
        top-left {:x x-far-left
                  :y (/ js/window.innerHeight 2)
                  :z 0}
        categories  (for [category catalog
                          :let [color (-> (* (rand) 0.5) (+ 0.25))
                                products (:products category)
                                id (str "category-" (:category/name category))

                                div [:div {:id    id
                                           :class "category"
                                           :style {:backgroundColor (str "rgb(0,127,127)")
                                                   :border-style "solid"
                                                   :border-color "white"
                                                   :width "40%"}}
                                     (for [p products]
                                       [:button {:id (:product/sku p)
                                                 :class "product"}
                                        [:img {:src (or (:url p) "http://www.creattor.com/files/10/652/drinks-icons-screenshots-1.png")
                                               :class "product-img"}]
                                        [:div  (:product/name p)]])]
                                css3d-object (div->css3d-object (c/html div))]]
                      (do
                        (four/add css3d-object)
                        css3d-object))
        get-category-container (fn [category-button]
                                 (let [category-name (. category-button -id)
                                       id (str "category-" category-name)]
                                   (four/id->object3d id)))

        set-active-category-container (fn [category-button]
                                        (let [category-container-css3dobj (get-category-container category-button)
                                              category-container-div (. category-container-css3dobj -element)
                                              category-container-width (. category-container-div -clientWidth)
                                              category-container-height (. category-container-div -clientHeight)
                                              
                                              ]
                                          (if (nil? @active-category-button)
                                            (do
                                              (reset! active-category-button category-button)
                                              (reset! active-category-container category-container-css3dobj)))
                                          
                                          (set! (.. @active-category-button -style -backgroundColor) nil)
                                          (morph [@active-category-container] :into off-screen-left)

                                          (reset! active-category-button category-button)
                                          (reset! active-category-container category-container-css3dobj)
                                          (set! (.. @active-category-button -style -backgroundColor) "rgb(100,100,100)")
                                          (morph [category-container-css3dobj] :into [{:x (+ x-far-left
                                                                                             (/ (. category-container-div -clientWidth)
                                                                                                2))
                                                                                       :y -40
                                                                                       :z 0}])
                                          ))]
    
    (four/add category-button-container-css3d-object)
    
    (doseq [category categories]
      (morph [category] :into off-screen-left))
    
    (doseq [category-button category-buttons]
      (dom/on category-button "click" #(set-active-category-container category-button)))

    (m/postpone (fn []
                  (set-active-category-container (first category-buttons))
                  (let [top-left (update-in top-left [:x] #(+ %
                                                              (/ (. category-button-container-div -clientWidth)
                                                                 2)
                                                              10))
                        top-left (update-in top-left [:y] #(+ %
                                                              (/ (. category-button-container-div -clientHeight)
                                                                 -2)
                                                              -10
                                                              ))]
                    (println "boo " top-left)
                    (println "w=" (. category-button-container-div -clientWidth))
                    (morph [category-button-container-css3d-object]
                           :into
                           [top-left])))
                1000)
    
    ))

(defmethod process-msg :catalog [[_ catalog]]
  (build-scene catalog))

(defn send-get-catalog []
  (dom/whenever-dom-ready #(ws/send! [:get-catalog true])))

(defn on-js-reload [])

(send-get-catalog)


