(ns four.client.layout
  (:require [four.client.three :as three :refer [IShape]]
            [four.client.table :as table]
            ))

(def PI (. js/Math -PI))

(defn div->css3d-object [div]
  (let [obj (three/CSS3DObject. div)]
    (set! (.. obj -position -x) (-> (* (rand) 4000) (- 2000)))
    (set! (.. obj -position -y) (-> (* (rand) 4000) (- 2000)))
    (set! (.. obj -position -z) (-> (* (rand) 4000) (- 2000)))
    obj))

(defn map->object3d [{:keys [x y z] :as point}]
  (let [obj (three/Object3D.)]
    (set! (.. obj -position -x) x)
    (set! (.. obj -position -y) y)
    (set! (.. obj -position -z) z)
    obj))

(def Table (let [elements (atom [])
                 css3d-objects (atom [])]
             (reify IShape
               (add [this element]
                    (swap! elements conj element)
                    (let [i (dec (count @elements))
                          j (nth table/coordinates i)
                          object3d (map->object3d {:x (-> (* (:x j) 140) (- 1330))
                                                   :y (-> (* (:y j) -180) (+ 1330))
                                                   :z 0})]
                      (swap! css3d-objects conj object3d)))
               (to-seq [this]
                       @css3d-objects))))

(def Sphere (let [elements (atom [])
                  css3d-objects (atom [])]
              (reify IShape
                (add [this element]
                     (let [size (-> @elements count inc)
                           v (three/Vector3.)]
                       (swap! elements conj element)
                       (reset! css3d-objects
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
                                     object3d)))))
                (to-seq [this]
                        @css3d-objects))))

(def Helix (let [elements (atom [])
                 css3d-objects (atom [])]
             (reify IShape
               (add [this element]
                    (swap! elements conj element)
                    
                    (let [i (-> @elements count dec)
                          v (three/Vector3.)
                          phi (* i 0.175 PI)
                          object (map->object3d {:x (* 900 (. js/Math sin phi))
                                                 :y (+ (* i -8)
                                                       450)
                                                 :z (* 900 (. js/Math cos phi))})]
                      (swap! css3d-objects conj object)
                      
                      (set! (. v -x) (* 2 (.. object -position -x)))
                      (set! (. v -y) (.. object -position -y))
                      (set! (. v -z) (* 2 (.. object -position -z)))
                      (. object lookAt v)))
               (to-seq [this]
                       @css3d-objects))))

(def Grid (let [elements (atom [])
                css3d-objects (atom [])]
            (reify IShape
              (add [this element]
                   (swap! elements conj element)
                   
                   (let [i (-> @elements count dec)
                         object (map->object3d {:x (- (* 400 (mod i 5))
                                                      800)
                                                :y (+ 800 (* -400 (mod (. js/Math floor (/ i 5))
                                                                       5)))
                                                :z (- (* 1000
                                                         (. js/Math floor (/ i 25)))
                                                      2000)})]
                     (swap! css3d-objects conj object)))
              (to-seq [this]
                      @css3d-objects))))
