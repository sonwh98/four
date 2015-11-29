(ns four.client.core
  (:require [four.client.three :as three]))

(defn map->object3d [{:keys [x y z] :as point}]
  (let [obj (three/Object3D.)]
    (set! (.. obj -position -x) x)
    (set! (.. obj -position -y) y)
    (set! (.. obj -position -z) z)
    obj))

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

(defn animate [animation-fn]
  ((fn animation-loop [time]
     (animation-fn time)
     (js/requestAnimationFrame animation-loop))))
