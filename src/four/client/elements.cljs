(ns four.client.elements
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [four.client.three :as three]
            [four.client.browser :as b]
            [four.client.layout :as layout :refer [IShape]]
            [four.client.table :as table]
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

(defn morph [css3d-objects _ topology]
  (.. three/tween removeAll)
  (doseq [[i obj] (map-indexed (fn [i e] [i e]) css3d-objects)
          :let [object3d (nth topology i)
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
        camera (three/PerspectiveCamera. 50 (/ (.-innerWidth b/window)
                                               (.-innerHeight b/window))
                                         10000
                                         1000)
        domElement (. renderer -domElement)
        _ (. (b/by-id "container") appendChild domElement)
        render-scene (fn [] (. renderer (render scene camera)))
        controls (three/TrackballControls. camera domElement)]
    (set! (.. camera -position -z) 3000)
    (set! (.. domElement -style -position) "absolute")
    (. renderer (setSize (. b/window -innerWidth)
                         (. b/window -innerHeight)))

    (set! (.. controls -rotateSpeed) 0.5)
    (set! (.. controls -minDistance) 500)
    (set! (.. controls -maxDistance) 6000)
    (.. controls (addEventListener "change" render-scene))

    (b/on (b/by-id "reset") "click" (fn [event]
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
    (b/on div "click" (fn [evt]
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


(defn create-scene [elements]
  (let [scene (three/Scene.)]
    (doseq [[i element] (map-indexed (fn [i element] [i element]) elements)
            :let [color (-> (* (rand) 0.5) (+ 0.25))
                  div [:div {:id    i
                             :class "element"
                             :style {:backgroundColor (str "rgba(0,127,127," color ")")}}
                       [:div {:class "number"} i]
                       [:div {:class "symbol"} (:element/symbol element)]
                       [:div {:class "details"} (:element/name element)]]
                  css3d-object (layout/div->css3d-object (c/html div))]]
      (.. scene (add css3d-object)))
    scene))

(defn on-click [shape morph-fn]
  (b/on (b/by-id (name shape)) "click" morph-fn))

(defn init [elements]
  (let [scene (create-scene elements)
        css3d-objects (seq (. scene -children))]
    (doseq [css3d-obj css3d-objects]
      (layout/add layout/Sphere css3d-obj)
      (layout/add layout/Table css3d-obj)
      (layout/add layout/Helix css3d-obj)
      (layout/add layout/Grid css3d-obj)
      (rotate css3d-obj :on-click))
    
    (on-click :table #(morph css3d-objects :into (layout/to-seq layout/Table)))
    (on-click :sphere #(morph css3d-objects :into (layout/to-seq layout/Sphere)))
    (on-click :helix #(morph css3d-objects :into (layout/to-seq layout/Helix)))
    (on-click :grid #(morph css3d-objects :into (layout/to-seq layout/Grid)))

    (setup-animation scene)
    (morph css3d-objects :into (layout/to-seq layout/Table))))

(defmethod process-msg :elements [[_ elements]]
  (init elements))

(m/on :dom/ready #(ws/send! [:get-elements true]))
