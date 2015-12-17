(ns four.client.core
  (:require [cljsjs.three]
            [cljsjs.tween]
            [four.messaging :as m]
            [four.client.dom :as dom]))

(defn get-aspect-ratio []
  (/ js/window.innerWidth
     js/window.innerHeight))

(defn degree->radian [degree]
  (* degree (/ js/Math.PI 180)))

(defn radian->degree [radian]
  (* radian (/ 180 js/Math.PI)))

(defn get-vertical-fov
  "vertical field of view in radians"
  [camera]
  (degree->radian (. camera -fov)))

(defn get-hortizontal-fov [camera]
  (let [aspect-ratio (get-aspect-ratio)
        vfov (get-vertical-fov camera)]
    (-> (/ vfov 2) js/Math.tan (* aspect-ratio) js/Math.atan (* 2))))

(defn get-visible-height [camera]
  (let [fov (get-vertical-fov camera)
        distance (.. camera -position -z)]
    (* 2 (js/Math.tan (/ fov 2)) distance)))

(defn get-visible-width [camera]
  (let [aspect-ratio (get-aspect-ratio)
        hfov (get-hortizontal-fov camera)
        distance (.. camera -position -z)]
    (* (/ hfov 2) js/Math.tan (* 2 distance))))

(defn calculate-fov [height distance]
  (->  height (/ distance) (/ 2) js/Math.atan (* 2)))

(defn animate [animation-fn]
  ((fn animation-loop [time]
     (animation-fn time)
     (js/requestAnimationFrame animation-loop))))


(defn init []
  (defonce scene (js/THREE.Scene.))
  (defonce camera (let [z-distance 1000
                        fov  (->  (calculate-fov js/window.innerHeight z-distance)
                                  radian->degree)]
                    (js/THREE.PerspectiveCamera. fov
                                                 (get-aspect-ratio)
                                                 1000
                                                 1)))
  (set! (.. camera -position -z) 1000)
  
  (defonce renderer (js/THREE.CSS3DRenderer.))
  (. renderer (setSize js/window.innerWidth
                       js/window.innerHeight))
  (def domElement (. renderer -domElement))
  (set! (.. domElement -style -position) "absolute")
  (. (dom/by-id "container") appendChild domElement)  

  (letfn [(render-scene []
                        (. renderer (render scene camera)))]
    (animate (fn [time]
               (.. js/TWEEN update)
               (render-scene)))))

(defonce id-index (atom {}))
(defn id->object3d [id]
  (@id-index id)
  )

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

(defn div->css3d-object [div]
  (let [css3d-obj (js/THREE.CSS3DObject. div)]
    (set! (.. css3d-obj -position -x) (-> (* (rand) 4000) (- 2000)))
    (set! (.. css3d-obj -position -y) (-> (* (rand) 4000) (- 2000)))
    (set! (.. css3d-obj -position -z) (-> (* (rand) 4000) (- 2000)))
    (swap! id-index assoc (. div -id) css3d-obj)
    css3d-obj))

(defn tween [property _ new-val]
  (let [duration 500]
    (.. (js/TWEEN.Tween. property)
        (to (clj->js new-val)
            (+ (* (rand) duration)
               duration))
        (easing (.. js/TWEEN -Easing -Exponential -InOut))
        (start))))

(defn morph [css3d-objects _ seq-of-points]
  (let [seq-of-object3d (map position-map->object3d seq-of-points)]
    (doseq [ [css3d-obj object3d] (partition 2 (interleave css3d-objects seq-of-object3d))
             :let [current-position (. css3d-obj -position)
                   current-rotation (. css3d-obj -rotation)
                   new-position (property->map (. object3d -position))
                   new-rotation (property->map (. object3d -rotation))]]
      (tween current-position :to new-position)
      (tween current-rotation :to new-rotation))))


(m/on :window/resize (fn []
                       (set!  (. camera -aspect) (get-aspect-ratio))
                       ;; (. renderer setSize js/window.innerWidth js/window.innerHeight)
                       ))
