(ns four.elements
  (:require [four.core :as f]
            [four.messaging :as m]
            [four.dom :as dom]
            [four.table :as table]
            [crate.core :as c]))

(enable-console-print!)

(def window js/window)
(def document js/document)

(def objects (atom []))
(def targets {:table (atom [])
              :sphere (atom [])})

(def renderer (f/CSS3DRenderer.))
(.. renderer (setSize (.. window -innerWidth)
                      (.. window -innerHeight)))
(set! (.. renderer -domElement -style -position) "absolute")


(def camera (f/PerspectiveCamera. 50 (/ (.-innerWidth window)
                                        (.-innerHeight window))
                                  10000
                                  1000))
(set! (.. camera -position -z) 3000)

(def scene (f/Scene.))

(.. scene (add camera))
(defn render []
  (.. renderer (render scene camera)))


(def controls (f/TrackballControls. camera (.. renderer -domElement)))
(set! (.. controls -rotateSpeed) 0.5)
(set! (.. controls -minDistance) 100)
(set! (.. controls -maxDistance) 6000)
(.. controls (addEventListener "change" render))


(defn map->object3d [ {:keys [x y z] :as point} ]
  (let [obj (f/Object3D.)]
    (set! (.. obj -position -x) x)
    (set! (.. obj -position -y) y)
    (set! (.. obj -position -z) z)
    obj))

(defn object3d->map [object3d]
  {:x (.. object3d -position -x)
   :y (.. object3d -position -y)
   :z (.. object3d -position -z)})

(defn transform [targets duration]
  (.. f/tween removeAll)
  (doseq [[i obj] (map-indexed (fn [i e] [i e]) @objects)
          :let [target (nth targets i)]]
    (.. (f/Tween. (.. obj -position))
        (to (clj->js (object3d->map  target))
            (+ (* (rand) duration)
               duration))
        (easing (.. f/tween -Easing -Exponential -InOut))
        (start)))

  )

(defn animate []
  (f/animate (fn [time]
               (.. f/tween update)
               (.. controls update)
               (render)
               ))
  )

(defn div->css3d-object [div]
  (let [obj (f/CSS3DObject. div)]
    (set! (.. obj -position -x) (-> (* (rand) 4000) (- 2000)))
    (set! (.. obj -position -y) (-> (* (rand) 4000) (- 2000)))
    (set! (.. obj -position -z) (-> (* (rand) 4000) (- 2000)))
    obj))

(defn init []
  (let [elements (map-indexed (fn [i element] [i element]) table/elements)
        length (count elements)
        pi (. js/Math -PI)]
    (doseq [[i element] elements
            :let [color (-> (* (rand) 0.5) (+ 0.25))
                  div [:div {:id    i
                             :class "element"
                             :style {:backgroundColor (str "rgba(0,127,127," color ")")}}
                       [:div {:class "number"} i]
                       [:div {:class "symbol"} (:element/symbol element)]
                       [:div {:class "details"} (:element/name element)]]
                  div-as-dom (c/html div)
                  css3d (div->css3d-object div-as-dom)]]
      (.. scene (add css3d))
      (swap! objects conj css3d)
      (swap! (:table targets) conj (map->object3d {:x (-> (* (:element/x element) 140) (- 1330))
                                                   :y (-> (* (:element/y element) -180) (+ 1330))
                                                   :z 0}))
      (swap! (:sphere targets)
             (fn [sphere]
               (let [phi (. js/Math acos (+ (/ (* 2 i) length)
                                            -1))
                     theta (* phi
                              (. js/Math sqrt (* length pi)))]

                 (conj sphere (map->object3d {:x (* 800 (. js/Math cos theta) (. js/Math sin phi))
                                              :y (* 800 (. js/Math sin phi) (. js/Math sin phi))
                                              :z (* 800 (. js/Math cos phi))})))))

      

      (dom/on div-as-dom "click" (fn [evt]
                                   (let [p (.. css3d -position clone)]
                                     (.. (f/Tween. (clj->js {:theta 0}))
                                         (to (clj->js {:theta (* 2 (.-PI js/Math))})
                                             1000)
                                         (easing (.. f/tween -Easing -Exponential -InOut))
                                         (onUpdate (fn [a]
                                                     (this-as this
                                                              (let [angle (js->clj this)]
                                                                (set! (.. css3d -rotation -y)
                                                                      (angle "theta"))))))
                                         (onComplete (fn []
                                                       (set! (.. css3d -rotation -y)
                                                             (* 2 (.-PI js/Math)))))
                                         (start))
                                     ;;(set!  (.. div -style -width) "90%")
                                     ;;(set!  (.. div -style -height) "90%")
                                     ;; (set! (.. camera -position -x) (.. p -x))
                                     ;; (set! (.. camera -position -y) (.. p -y))
                                     ;; (set! (.. camera -position -z) 500)
                                     ;; (set! (.. controls -target) p)
                                     (.. camera (lookAt p))))))

    (render)))



(init)
(transform @(:table targets) 2000)
(animate)

                                        ;(transform @(:table targets) 2000)
(dom/on (dom/by-id "table") "click" (fn [event]
                                      (transform @(:table targets) 2000)))

(dom/on (dom/by-id "sphere") "click" (fn [event]
                                       (transform @(:sphere targets) 2000)))

(.. (dom/by-id "container") (appendChild (.-domElement renderer)))
