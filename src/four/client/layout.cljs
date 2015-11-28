(ns four.client.layout
  (:require [four.client.three :as three]
            [four.client.table :as table]))

(def PI (. js/Math -PI))

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

(defn create-table [elements]
  (for [i (range (count elements))
        :let [j (nth table/coordinates i)]]
    (map->object3d {:x (-> (* (:x j) 140) (- 1330))
                    :y (-> (* (:y j) -180) (+ 1330))
                    :z 0})))

(defn create-sphere [elements]
  (let [size (count elements)
        v (three/Vector3.)]
    (for [i (range size)
          :let [phi (. js/Math acos (+ (/ (* 2 i) size)
                                       -1))
                theta (* phi
                         (. js/Math sqrt (* size PI)))
                object3d (map->object3d {:x (* 800 (. js/Math cos theta) (. js/Math sin phi))
                                         :y (* 800 (. js/Math sin theta) (. js/Math sin phi))
                                         :z (* 800 (. js/Math cos phi))})]]
      (do (.. v (copy (. object3d -position)) (multiplyScalar 2))
          (. object3d (lookAt v))
          object3d))))

(defn create-helix [elements]
  (let [v (three/Vector3.)
        size (count elements)]
    (for [i (range size)
          :let [phi (* i 0.175 PI)
                object (map->object3d {:x (* 900 (. js/Math sin phi))
                                       :y (+ (* i -8)
                                             450)
                                       :z (* 900 (. js/Math cos phi))})]]

      (do (set! (. v -x) (* 2 (.. object -position -x)))
          (set! (. v -y) (.. object -position -y))
          (set! (. v -z) (* 2 (.. object -position -z)))
          (. object lookAt v)
          object))))

(defn create-grid [elements]
  (let [size (count elements)]
    (for [i (range size)]
      (map->object3d {:x (- (* 400 (mod i 5))
                            800)
                      :y (+ 800 (* -400 (mod (. js/Math floor (/ i 5))
                                             5)))
                      :z (- (* 1000
                               (. js/Math floor (/ i 25)))
                            2000)}))))
