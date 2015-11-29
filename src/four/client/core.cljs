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

(defn object3d->position-map [object3d]
  (property->map (. object3d -position)))

(defn object3d->rotation-map [object3d]
  (property->map (. object3d -rotation)))

(defn div->css3d-object [div]
  (let [css3d-obj (js/THREE.CSS3DObject. div)]
    (set! (.. css3d-obj -position -x) (-> (* (rand) 4000) (- 2000)))
    (set! (.. css3d-obj -position -y) (-> (* (rand) 4000) (- 2000)))
    (set! (.. css3d-obj -position -z) (-> (* (rand) 4000) (- 2000)))
    css3d-obj))

(defn tween [property _ to]
  (let [duration 1000]
    (.. (js/TWEEN.Tween. property)
        (to (clj->js to)
            (+ (* (rand) duration)
               duration))
        (easing (.. js/TWEEN -Easing -Exponential -InOut))
        (start))))

(defn morph [css3d-objects _ shape]
  (.. js/TWEEN removeAll)
  (doseq [[i css3d-obj] (map-indexed (fn [i e] [i e]) css3d-objects)
          :let [object3d (nth shape i)
                duration 1000]]

    (tween (. css3d-obj -position) :to (object3d->position-map object3d))
    (tween (. css3d-obj -rotation) :to (object3d->rotation-map object3d))))

(defn animate [animation-fn]
  ((fn animation-loop [time]
     (animation-fn time)
     (js/requestAnimationFrame animation-loop))))
