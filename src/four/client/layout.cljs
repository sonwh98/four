(ns four.client.layout
  (:require [four.client.core :as four]
            [four.client.table :as table]))

(def PI (. js/Math -PI))

(defn create-table []
  (for [i (range)
        :let [j (nth table/coordinates i)]]
    (four/position-map->object3d {:x (-> (* (:x j) 140) (- 1330))
                                  :y (-> (* (:y j) -180) (+ 1330))
                                  :z 0})))

(defn create-sphere [elements]
  (let [size (count elements)
        v (js/THREE.Vector3.)]
    (for [i (range size)
          :let [phi (. js/Math acos (+ (/ (* 2 i) size)
                                       -1))
                theta (* phi
                         (. js/Math sqrt (* size PI)))
                object3d (four/position-map->object3d {:x (* 800 (. js/Math cos theta) (. js/Math sin phi))
                                                       :y (* 800 (. js/Math sin theta) (. js/Math sin phi))
                                                       :z (* 800 (. js/Math cos phi))})]]
      (do (.. v (copy (. object3d -position)) (multiplyScalar 2))
          (. object3d (lookAt v))
          object3d))))

(defn create-helix [elements]
  (let [v (js/THREE.Vector3.)
        size (count elements)]
    (for [i (range size)
          :let [phi (* i 0.175 PI)
                object (four/position-map->object3d {:x (* 900 (. js/Math sin phi))
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
      (four/position-map->object3d {:x (- (* 400 (mod i 5))
                                          800)
                                    :y (+ 800 (* -400 (mod (. js/Math floor (/ i 5))
                                                           5)))
                                    :z (- (* 1000
                                             (. js/Math floor (/ i 25)))
                                          2000)}))))
