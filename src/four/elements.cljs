(ns four.elements
  (:require [four.three :as three]
            [four.messaging :as m]
            [four.dom :as dom]
            [four.table :as table]
            [four.transit :as t]
            [crate.core :as c]))

(enable-console-print!)

(def window js/window)
(def PI (. js/Math -PI))

(def css3d-objects (atom []))
(def topologies {:table  (atom [])
                 :sphere (atom [])
                 :helix  (atom [])
                 :grid   (atom [])})

(def camera (three/PerspectiveCamera. 50 (/ (.-innerWidth window)
                                        (.-innerHeight window))
                                  10000
                                  1000))
(set! (.. camera -position -z) 3000)

(def scene (three/Scene.))

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

(defn morph-into [topology]
  (.. three/tween removeAll)
  (doseq [[i obj] (map-indexed (fn [i e] [i e]) @css3d-objects)
          :let [object3d (nth topology i)
                duration 2000]]
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

(defn setup-animation []
  (let [renderer (three/CSS3DRenderer.)
        domElement (. renderer -domElement)
        _ (. (dom/by-id "container") appendChild domElement)
        render-scene (fn [] (. renderer (render scene camera)))
        controls (three/TrackballControls. camera domElement)]
    (set! (.. domElement -style -position) "absolute")
    (. renderer (setSize (. window -innerWidth)
                         (. window -innerHeight)))

    (set! (.. controls -rotateSpeed) 0.5)
    (set! (.. controls -minDistance) 500)
    (set! (.. controls -maxDistance) 6000)
    (.. controls (addEventListener "change" render-scene))

    (def controls controls)
    
    (three/animate (fn [time]
                 (.. three/tween update)
                 (.. controls update)
                 (render-scene)))))

(defn div->css3d-object [div]
  (let [obj (three/CSS3DObject. div)]
    (set! (.. obj -position -x) (-> (* (rand) 4000) (- 2000)))
    (set! (.. obj -position -y) (-> (* (rand) 4000) (- 2000)))
    (set! (.. obj -position -z) (-> (* (rand) 4000) (- 2000)))
    obj))

(defn create-table [i]
  (let [point (nth table/coordinates i)]
    (swap! (:table topologies) conj (map->object3d {:x (-> (* (:x point) 140) (- 1330))
                                                    :y (-> (* (:y point) -180) (+ 1330))
                                                    :z 0}))))

(defn create-sphere [i length]
  (let [v (three/Vector3.)]
    (swap! (:sphere topologies)
           (fn [sphere]
             (let [phi (. js/Math acos (+ (/ (* 2 i) length)
                                          -1))
                   theta (* phi
                            (. js/Math sqrt (* length PI)))
                   object3d (map->object3d {:x (* 800 (. js/Math cos theta) (. js/Math sin phi))
                                            :y (* 800 (. js/Math sin theta) (. js/Math sin phi))
                                            :z (* 800 (. js/Math cos phi))})]
               (.. v (copy (. object3d -position)) (multiplyScalar 2))
               (. object3d (lookAt v))
               (conj sphere object3d))))))

(defn create-helix [i]
  (let [v (three/Vector3.)]
    (swap! (:helix topologies) (fn [helix]
                                 (let [phi (* i 0.175 PI)
                                       object (map->object3d {:x (* 900 (. js/Math sin phi))
                                                              :y (+ (* i -8)
                                                                    450)
                                                              :z (* 900 (. js/Math cos phi))})]
                                   (set! (. v -x) (* 2 (.. object -position -x)))
                                   (set! (. v -y) (.. object -position -y))
                                   (set! (. v -z) (* 2 (.. object -position -z)))
                                   (. object lookAt v)
                                   (conj helix object)))))
  )

(defn create-grid [i]
  (swap! (:grid topologies) (fn [grid]
                              (let [object3d (map->object3d {:x (- (* 400 (mod i 5))
                                                                   800)
                                                             :y (+ 800 (* -400 (mod (. js/Math floor (/ i 5))
                                                                                    5)))
                                                             :z (- (* 1000
                                                                      (. js/Math floor (/ i 25)))
                                                                   2000)})]
                                (conj grid object3d)))))

(defn rotate [css3d-object _]
  (let [div (. css3d-object -element)]
    (dom/on div "click" (fn [evt]
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
                            (.. camera (lookAt p)))))))


(defn on-click-change-to [shape]
  (dom/on (dom/by-id (name shape)) "click" (fn [event]
                                             (morph-into @(shape topologies)))))

(defn create-topologies [elements]
  (doseq [[i element] (map-indexed (fn [i element] [i element]) elements)
          :let [color (-> (* (rand) 0.5) (+ 0.25))
                div [:div {:id    i
                           :class "element"
                           :style {:backgroundColor (str "rgba(0,127,127," color ")")}}
                     [:div {:class "number"} i]
                     [:div {:class "symbol"} (:element/symbol element)]
                     [:div {:class "details"} (:element/name element)]]
                css3d-object (div->css3d-object (c/html div))]]
    (.. scene (add css3d-object))
    (swap! css3d-objects conj css3d-object)

    (create-table i)
    (create-sphere i (count elements))
    (create-helix i)
    (create-grid i)
    (rotate css3d-object :when-clicked)))

(defn init [elements]
  (create-topologies elements)
  (on-click-change-to :table)
  (on-click-change-to :sphere)
  (on-click-change-to :helix)
  (on-click-change-to :grid)
  
  (dom/on (dom/by-id "reset") "click" (fn [event]
                                        (println "reset")
                                        (. controls reset)
                                        ;; (set! (.. camera -position -x) 0)
                                        ;; (set! (.. camera -position -y) 0)
                                        ;; (set! (.. camera -position -z) 3000)
                                        ))
  
  (setup-animation)
  (morph-into @(:table topologies)))

(m/on :dom/content-loaded (fn []
                            (let [ws  (js/WebSocket. "ws://localhost:9090")]
                              (set! (. ws -onopen) (fn [evt]
                                                     (println "openopen " evt)
                                                     (. ws send "foo")))
                              (set! (. ws -onclose) (fn [evt]
                                                      (println "onclose " evt)
                                                      ))
                              
                              (set! (. ws -onmessage) (fn [evt]
                                                        (let [data-str (. evt -data)
                                                              elements (t/deserialize data-str)]
                                                          (init elements)
                                                          )))
                              
                              (set! (. ws -onerror) (fn [evt]
                                                      (println "onerror " evt)
                                                      )))))
