(ns four.client.menu
  (:require [four.client.core :as four :refer [morph div->css3d-object]]
            [four.client.dom :as dom]
            [four.client.layout :as layout]
            [four.client.ws :as ws :refer [process-msg]]
            [four.messaging :as m]
            [crate.core :as c]))


(declare scene)
(declare renderer)
(declare camera)

(defn init []
  (def scene (js/THREE.Scene.))
  (def camera (js/THREE.PerspectiveCamera. 60 (/ (.-innerWidth dom/window)
                                                 (.-innerHeight dom/window))
                                           1000
                                           1))
  (set! (.. camera -position -z) 1000)
  
  (def renderer (js/THREE.CSS3DRenderer.))
  (. renderer (setSize (. dom/window -innerWidth)
                       (. dom/window -innerHeight)))
  (def domElement (. renderer -domElement))
  (set! (.. domElement -style -position) "absolute")
  (. (dom/by-id "container") appendChild domElement)

  (letfn [(render-scene []
                        (. renderer (render scene camera)))]
    (four/animate (fn [time]
                    (.. js/TWEEN update)
                    (render-scene)))))

(init)

(defn populate [scene _ catalog]
  (doseq [[i category] (map-indexed (fn [i category] [i category]) catalog)
          :let [color (-> (* (rand) 0.5) (+ 0.25))
                products (:products category)
                div [:div {:id    i
                           :class "category"
                           :style {:backgroundColor (str "rgb(0,127,127)")
                                   :border-style "solid"
                                   :border-color "white"
                                   :width "300px"}}
                     (for [p products]
                       [:button {:class "product"} (:product/name p)])]
                css3d-object (div->css3d-object (c/html div))]]
    (println products)
    (.. scene (add css3d-object)))
  scene)

(defn reset-camera []
  ;(. controls reset)
   )
  
(defn on-click [button-id callback-fn]
  (dom/on (dom/by-id (name button-id)) "click" callback-fn))

(defn build-scene [catalog]
  (populate scene :with catalog)
  (let [css3d-objects (seq (. scene -children))
        size (count css3d-objects)
        table (layout/create-table)
        sphere (layout/create-sphere size)
        helix (layout/create-helix size)
        grid (layout/create-grid size)
        m (layout/create-menu)
        ]
        
    (on-click :table #(morph css3d-objects :into table))
    (on-click :sphere #(morph css3d-objects :into sphere))
    (on-click :helix #(morph css3d-objects :into helix))
    (on-click :grid #(morph css3d-objects :into grid))
    (on-click :reset reset-camera)

    (morph css3d-objects :into m))
  )

(defmethod process-msg :catalog [[_ catalog]]
  (build-scene catalog))

(defn send-get-catalog []
  (println "send-get-catalog")
  (dom/whenever-dom-ready #(ws/send! [:get-catalog true])))

(defn on-js-reload [])

(send-get-catalog)