(ns four.client.elements
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [four.client.three :as three]
            [four.client.util :as util]
            [four.client.layout :as layout :refer [IShape]]
            [four.client.ws :as ws :refer [process-msg]]
            [chord.client :refer [ws-ch]]
            [four.messaging :as m]
            [crate.core :as c]
            [cljs.core.async :refer [<! >! put! chan]]))

(enable-console-print!)

(def PI (. js/Math -PI))

(defn object3d->map [object3d]
  {:x (.. object3d -position -x)
   :y (.. object3d -position -y)
   :z (.. object3d -position -z)})

(defn div->css3d-object [div]
  (let [obj (three/CSS3DObject. div)]
    (set! (.. obj -position -x) (-> (* (rand) 4000) (- 2000)))
    (set! (.. obj -position -y) (-> (* (rand) 4000) (- 2000)))
    (set! (.. obj -position -z) (-> (* (rand) 4000) (- 2000)))
    obj))

(defn morph [css3d-objects _ shape]
  (.. three/tween removeAll)
  (doseq [[i obj] (map-indexed (fn [i e] [i e]) css3d-objects)
          :let [object3d (nth shape i)
                duration 1000]]
    (.. (three/Tween. (. obj -position))
        (to (clj->js (object3d->map object3d))
            (+ (* (rand) duration)
               duration))
        (easing (.. three/tween -Easing -Exponential -InOut))
        (start))

    (.. (three/Tween. (. obj -rotation))
        (to (clj->js {:x (.. object3d -rotation -x)
                      :y (.. object3d -rotation -y)
                      :z (.. object3d -rotation -z)})
            (+ (* (rand) duration)
               duration))
        (easing (.. three/tween -Easing -Exponential -InOut))
        (start))))

(defn setup-animation [scene]
  (let [renderer (three/CSS3DRenderer.)
        camera (three/PerspectiveCamera. 50 (/ (.-innerWidth util/window)
                                               (.-innerHeight util/window))
                                         10000
                                         1000)
        domElement (. renderer -domElement)
        _ (. (util/by-id "container") appendChild domElement)
        render-scene (fn [] (. renderer (render scene camera)))
        controls (three/TrackballControls. camera domElement)]
    (set! (.. camera -position -z) 3000)
    (set! (.. domElement -style -position) "absolute")
    (. renderer (setSize (. util/window -innerWidth)
                         (. util/window -innerHeight)))

    (set! (.. controls -rotateSpeed) 0.5)
    (set! (.. controls -minDistance) 500)
    (set! (.. controls -maxDistance) 6000)
    (.. controls (addEventListener "change" render-scene))

    (util/on (util/by-id "reset") "click" (fn [event]
                                      (. controls reset)
                                      (set! (.. controls -rotateSpeed) 0.5)
                                      (set! (.. controls -minDistance) 500)
                                      (set! (.. controls -maxDistance) 6000)

                                      (set! (.. camera -position -x) 0)
                                      (set! (.. camera -position -y) 0)
                                      (set! (.. camera -position -z) 3000)))
    
    (three/animate (fn [time]
                     (.. three/tween update)
                     (.. controls update)
                     (render-scene)))))

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

(def scene (three/Scene.))
(defn init [elements]
  (let [css3d-objects (populate-scene scene :with elements)]
    (doseq [css3d-obj css3d-objects]
      (layout/add layout/Sphere css3d-obj)
      (layout/add layout/Table css3d-obj)
      (layout/add layout/Helix css3d-obj)
      (layout/add layout/Grid css3d-obj)
      (rotate css3d-obj :on-click))
    
    (on-click :table #(morph css3d-objects :into (seq layout/Table)))
    (on-click :sphere #(morph css3d-objects :into (seq layout/Sphere)))
    (on-click :helix #(morph css3d-objects :into (seq layout/Helix)))
    (on-click :grid #(morph css3d-objects :into (seq layout/Grid)))

    (setup-animation scene)
    (morph css3d-objects :into (seq layout/Table))
    scene))

(defmethod process-msg :elements [[_ elements]]
  (init elements))

(m/on :dom/ready #(ws/send! [:get-elements true]))
