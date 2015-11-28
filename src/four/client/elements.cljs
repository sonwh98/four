(ns four.client.elements
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [four.client.three :as three]
            [four.client.util :as util]
            [four.client.layout :as layout :refer [morph div->css3d-object]]
            [four.client.ws :as ws :refer [process-msg]]
            [chord.client :refer [ws-ch]]
            [four.messaging :as m]
            [crate.core :as c]
            [cljs.core.async :refer [<! >! put! chan]]))

(enable-console-print!)

(def PI (. js/Math -PI))

(def renderer (three/CSS3DRenderer.))
(. renderer (setSize (. util/window -innerWidth)
                     (. util/window -innerHeight)))


(def domElement (. renderer -domElement))
(set! (.. domElement -style -position) "absolute")
(. (util/by-id "container") appendChild domElement)

(def camera (three/PerspectiveCamera. 50 (/ (.-innerWidth util/window)
                                             (.-innerHeight util/window))
                                       10000
                                       1000))
(set! (.. camera -position -z) 4000)

(def controls (three/TrackballControls. camera domElement))
(set! (.. controls -rotateSpeed) 0.5)
(set! (.. controls -minDistance) 500)
(set! (.. controls -maxDistance) 6000)

(util/on (util/by-id "reset") "click" (fn [event]
                                      (. controls reset)
                                      (set! (.. controls -rotateSpeed) 0.5)
                                      (set! (.. controls -minDistance) 500)
                                      (set! (.. controls -maxDistance) 6000)

                                      (set! (.. camera -position -x) 0)
                                      (set! (.. camera -position -y) 0)
                                      (set! (.. camera -position -z) 3000)))

(def scene (three/Scene.))

(defn render-scene []
  (. renderer (render scene camera)))

(.. controls (addEventListener "change" render-scene))

(three/animate (fn [time]
                 (.. three/tween update)
                 (.. controls update)
                 (render-scene)))

(defn rotate [css3d-object _]
  (let [div (. css3d-object -element)]
    (util/on div "click" (fn [evt]
                        (let [p (.. css3d-object -position clone)]
                          (.. (three/Tween. (clj->js {:theta 0}))
                              (to (clj->js {:theta (* 2 PI)})
                                  1000)
                              (easing (.. three/tween -Easing -Exponential -InOut))
                              (onUpdate (fn [a]
                                          (this-as this
                                                   (let [angle (js->clj this)]
                                                     (set! (.. css3d-object -rotation -y)
                                                           (angle "theta"))))))
                              (onComplete (fn []
                                            (set! (.. css3d-object -rotation -y)
                                                  (* 2 PI))))
                              (start))
                          ;;(set!  (.. div -style -width) "90%")
                          ;;(set!  (.. div -style -height) "90%")
                          ;; (set! (.. camera -position -x) (.. p -x))
                          ;; (set! (.. camera -position -y) (.. p -y))
                          ;; (set! (.. camera -position -z) 500)
                          ;; (set! (.. controls -target) p)
                          ;; (.. camera (lookAt p))
                          )))))


(defn populate-scene [scene _ elements]
  (doseq [[i element] (map-indexed (fn [i element] [i element]) elements)
          :let [color (-> (* (rand) 0.5) (+ 0.25))
                div [:div {:id    i
                           :class "element"
                           :style {:backgroundColor (str "rgba(0,127,127," color ")")}}
                     [:div {:class "number"} i]
                     [:div {:class "symbol"} (:element/symbol element)]
                     [:div {:class "details"} (:element/name element)]]
                css3d-object (div->css3d-object (c/html div))]]
    (.. scene (add css3d-object)))
  (seq (. scene -children)))

(defn on-click [shape morph-fn]
  (util/on (util/by-id (name shape)) "click" morph-fn))


(defn init [elements]
  (let [css3d-objects (populate-scene scene :with elements)
        table (layout/create-table elements)
        sphere (layout/create-sphere elements)
        helix (layout/create-helix elements)
        grid (layout/create-grid elements)]
    (doseq [css3d-obj css3d-objects]
      (rotate css3d-obj :on-click))
    
    (on-click :table #(morph css3d-objects :into table))
    (on-click :sphere #(morph css3d-objects :into sphere))
    (on-click :helix #(morph css3d-objects :into helix))
    (on-click :grid #(morph css3d-objects :into grid))

    (morph css3d-objects :into table))) 

(defmethod process-msg :elements [[_ elements]]
  (init elements))

(m/on :dom/ready #(ws/send! [:get-elements true]))
