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
(def targets {:table (atom [])})

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


(defn transform [target duration]
  (.. f/tween removeAll)
  (doseq [[i o] (map-indexed (fn [i e] [i e]) @objects)
          :let [final-point (nth target i)]]
    (.. (f/Tween. (.. o -position))
        (to (clj->js final-point)
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
        divs (for [[i element] elements
                   :let [color (-> (* (rand) 0.5) (+ 0.25))
                         div [:div {:id i
                                    :class "element"
                                    :style {:backgroundColor (str "rgba(0,127,127," color ")")}}
                              [:div {:class "number"} i]
                              [:div {:class "symbol"} (:element/symbol element)]
                              [:div {:class "details"} (:element/name element)]]
                         div-as-dom (c/html div)]]
               div-as-dom)
        css3d-objects (map div->css3d-object divs)]
    
    (doseq [[div css3d-obj] (partition 2 (interleave divs css3d-objects))]
      (.. scene (add css3d-obj))
      (dom/on div "click" (fn [evt]
                            (let [p (.. css3d-obj -position clone)]
                              (.. (f/Tween. (clj->js {:theta  0}))
                                  (to (clj->js {:theta (* 2  (.-PI js/Math))})
                                      2000)
                                  (easing (.. f/tween -Easing -Exponential -InOut))
                                  (onUpdate (fn [a]
                                              (this-as this
                                                       (let [angle (js->clj this)]
                                                         (set! (.. css3d-obj -rotation -y)
                                                               (angle "theta"))))))
                                  (onComplete (fn []
                                                (set! (.. css3d-obj -rotation -y)
                                                      (* 2  (.-PI js/Math)))))
                                  (start))
                              ;;(set!  (.. div -style -width) "90%")
                              ;;(set!  (.. div -style -height) "90%")
                              ;; (set! (.. camera -position -x) (.. p -x))
                              ;; (set! (.. camera -position -y) (.. p -y))
                              ;; (set! (.. camera -position -z) 500)
                              ;; (set! (.. controls -target) p)
                              (.. camera (lookAt p))))))
    
    (reset! objects css3d-objects)
    (reset! (:table targets) (for [[i e] elements]
                               {:x (-> (* (:element/x e) 140) (- 1330))
                                :y (-> (* (:element/y e) -180) (+ 1330))
                                :z 0}))
    (render)))



(init)
(transform @(:table targets) 2000)
(animate)

;(transform @(:table targets) 2000)
(dom/on (dom/by-id "table") "click" (fn [event]
                                      (transform @(:table targets) 2000)))

(.. (dom/by-id "container") (appendChild (.-domElement renderer)))




