(ns four.client.core
  (:require [cljsjs.three]
            [cljsjs.tween]))

(defn map->object3d [{:keys [x y z] :as point}]
  (let [obj (js/THREE.Object3D.)]
    (set! (.. obj -position -x) x)
    (set! (.. obj -position -y) y)
    (set! (.. obj -position -z) z)
    obj))

(defn object3d->map [object3d]
  {:x (.. object3d -position -x)
   :y (.. object3d -position -y)
   :z (.. object3d -position -z)})

(defn div->css3d-object [div]
  (let [obj (js/THREE.CSS3DObject. div)]
    (set! (.. obj -position -x) (-> (* (rand) 4000) (- 2000)))
    (set! (.. obj -position -y) (-> (* (rand) 4000) (- 2000)))
    (set! (.. obj -position -z) (-> (* (rand) 4000) (- 2000)))
    obj))

(defn morph [css3d-objects _ shape]
  (.. js/TWEEN removeAll)
  (doseq [[i obj] (map-indexed (fn [i e] [i e]) css3d-objects)
          :let [object3d (nth shape i)
                duration 1000]]
    (.. (js/TWEEN.Tween. (. obj -position))
        (to (clj->js (object3d->map object3d))
            (+ (* (rand) duration)
               duration))
        (easing (.. js/TWEEN -Easing -Exponential -InOut))
        (start))

    (.. (js/TWEEN.Tween. (. obj -rotation))
        (to (clj->js {:x (.. object3d -rotation -x)
                      :y (.. object3d -rotation -y)
                      :z (.. object3d -rotation -z)})
            (+ (* (rand) duration)
               duration))
        (easing (.. js/TWEEN -Easing -Exponential -InOut))
        (start))))

(defn animate [animation-fn]
  ((fn animation-loop [time]
     (animation-fn time)
     (js/requestAnimationFrame animation-loop))))
