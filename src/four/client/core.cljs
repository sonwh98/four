(ns four.client.core
  (:require [cljsjs.three]
            [cljsjs.tween]))

(defn position-map->object3d [{:keys [x y z] :as position}]
  (let [object3d (js/THREE.Object3D.)]
    (set! (.. object3d -position -x) x)
    (set! (.. object3d -position -y) y)
    (set! (.. object3d -position -z) z)
    object3d))

(defn property->map [property]
  {:x (aget property "x")
   :y (aget property "y")
   :z (aget property "z")})

(defn div->css3d-object [div]
  (let [css3d-obj (js/THREE.CSS3DObject. div)]
    (set! (.. css3d-obj -position -x) (-> (* (rand) 4000) (- 2000)))
    (set! (.. css3d-obj -position -y) (-> (* (rand) 4000) (- 2000)))
    (set! (.. css3d-obj -position -z) (-> (* (rand) 4000) (- 2000)))
    css3d-obj))

(defn tween [property _ new-val]
  (let [duration 1000]
    (.. (js/TWEEN.Tween. property)
        (to (clj->js new-val)
            (+ (* (rand) duration)
               duration))
        (easing (.. js/TWEEN -Easing -Exponential -InOut))
        (start))))

(defn morph [css3d-objects _ shape]
  (doseq [ [css3d-obj object3d] (partition 2 (interleave css3d-objects shape))
           :let [current-position (. css3d-obj -position)
                 current-rotation (. css3d-obj -rotation)
                 new-position (property->map (. object3d -position))
                 new-rotation (property->map (. object3d -rotation))]]
    (tween current-position :to new-position)
    (tween current-rotation :to new-rotation)))

(defn animate [animation-fn]
  ((fn animation-loop [time]
     (animation-fn time)
     (js/requestAnimationFrame animation-loop))))
