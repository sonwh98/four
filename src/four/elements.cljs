(ns four.elements
  (:require [four.core :as f]
            [four.messaging :as m]
            [four.dom :as dom]
            [four.table :as table]))

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
          :let [t (nth target i)]]
    (.. (f/Tween. (.. o -position))
        (to (clj->js {:x (.. t -position -x)
                      :y (.. t -position -y)
                      :z (.. t -position -z)})
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

(defn create-element [num element]
  (let [div (.. document (createElement "div"))
        _ (set! (.. div -className) "element")
        rand-float (-> (* (rand) 0.5) (+ 0.25))
        _ (set! (.. div -style -backgroundColor) (str "rgba(0,127,127," rand-float ")"))

        number (.. document (createElement "div"))
        _ (set! (.. number -className) "number")
        _ (set! (.. number -textContent) num)
        _ (.. div (appendChild number))

        sym (.. document (createElement "div"))
        _ (set! (.. sym -className) "symbol")
        _ (set! (.. sym -textContent) (:element/symbol element))
        _ (.. div (appendChild sym))


        details (.. document (createElement "div"))
        _ (set! (.. details -className) "details")
        _ (set! (.. details -innerHTML) (:element/name element))
        _ (.. div (appendChild details))

        obj (f/CSS3DObject. div)
        _ (set! (.. obj -position -x) (-> (* (rand) 4000) (- 2000)))
        _ (set! (.. obj -position -y) (-> (* (rand) 4000) (- 2000)))
        _ (set! (.. obj -position -z) (-> (* (rand) 4000) (- 2000)))
        _ (.. scene (add obj))]
    (dom/on div "click" (fn [evt]
                          (let [p (.. obj -position clone)]
                            (set! (.. camera -position -x) (.. p -x))
                            (set! (.. camera -position -y) (.. p -y))
                            (set! (.. camera -position -z) 100)
                            (set! (.. controls -target) p)
                            (.. camera (lookAt p))
                            )
                          
                          ))
    obj))

(defn init []
  (reset! objects (doall (for [[i e] (map-indexed (fn [i e] [i e]) table/elements)]
                           (create-element i e)
                           )))
  (reset! (:table targets) (doall (for [[i e] (map-indexed (fn [i e] [i e]) table/elements)
                                        :let [obj2 (f/Object3D.)
                                              x (-> (* (:element/x e) 140) (- 1330))
                                              y (-> (* (:element/y e) -180) (+ 1330))
                                              _ (set! (.. obj2 -position -x) x)
                                              _ (set! (.. obj2 -position -y) y)]]
                                    obj2)))
  (render))



(init)
(transform @(:table targets) 2000)
(animate)

;(transform @(:table targets) 2000)
(dom/on (dom/by-id "table") "click" (fn [event]
                                      (transform @(:table targets) 2000)))

(.. (dom/by-id "container") (appendChild (.-domElement renderer)))




