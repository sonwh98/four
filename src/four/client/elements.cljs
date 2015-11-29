(ns four.client.elements
  (:require [four.client.core :as four :refer [morph div->css3d-object]]
            [four.client.dom :as dom]
            [four.client.layout :as layout]
            [four.client.ws :as ws :refer [process-msg]]
            [four.messaging :as m]
            [crate.core :as c]))

(enable-console-print!)

(def renderer (js/THREE.CSS3DRenderer.))
(. renderer (setSize (. dom/window -innerWidth)
                     (. dom/window -innerHeight)))


(def domElement (. renderer -domElement))
(set! (.. domElement -style -position) "absolute")
(. (dom/by-id "container") appendChild domElement)

(def scene (js/THREE.Scene.))
(def camera (js/THREE.PerspectiveCamera. 50 (/ (.-innerWidth dom/window)
                                               (.-innerHeight dom/window))
                                         10000
                                         1000))
(set! (.. camera -position -z) 4000)

(def controls (js/THREE.TrackballControls. camera domElement))
(set! (.. controls -rotateSpeed) 0.5)
(set! (.. controls -minDistance) 500)
(set! (.. controls -maxDistance) 6000)

(defn render-scene []
  (. renderer (render scene camera)))

(four/animate (fn [time]
                (.. js/TWEEN update)
                (.. controls update)
                (render-scene)))

(defn rotate [css3d-object _]
  (let [div (. css3d-object -element)
        PI (. js/Math -PI) ]
    (dom/on div "click" (fn [evt]
                          (let [p (.. css3d-object -position clone)]
                            (.. (js/TWEEN.Tween. (clj->js {:theta 0}))
                                (to (clj->js {:theta (* 2 PI)})
                                    1000)
                                (easing (.. js/TWEEN -Easing -Exponential -InOut))
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
                            ;; (.. camera (lookAt p))
                            )))))

(defn populate [scene _ elements]
  (doseq [[i element] (map-indexed (fn [i element] [i element]) elements)
          :let [color (-> (* (rand) 0.5) (+ 0.25))
                div [:div {:id    i
                           :class "element"
                           :style {:backgroundColor (str "rgba(0,127,127," color ")")}}
                     [:div {:class "number"} i]
                     [:div {:class "symbol"} (:element/symbol element)]
                     [:div {:class "details"} (:element/name element)]]
                css3d-object (div->css3d-object (c/html div))]]
    (.. scene (add css3d-object)))
  scene)

(defn on-click [button-id callback-fn]
  (dom/on (dom/by-id (name button-id)) "click" callback-fn))

(defn reset-controls []
  (. controls reset)
  (set! (.. controls -rotateSpeed) 0.5)
  (set! (.. controls -minDistance) 500)
  (set! (.. controls -maxDistance) 6000)

  (set! (.. camera -position -x) 0)
  (set! (.. camera -position -y) 0)
  (set! (.. camera -position -z) 3000))

(defn init [elements]
  (let [scene (populate scene :with elements)
        css3d-objects (seq (. scene -children))
        table (layout/create-table elements)
        sphere (layout/create-sphere elements)
        helix (layout/create-helix elements)
        grid (layout/create-grid elements)]
    (doseq [css3d-obj css3d-objects]
      (rotate css3d-obj :on-click))
    
    (on-click :table #(morph css3d-objects :into table))
    (on-click :sphere #(morph css3d-objects :into sphere))
    (on-click :helix #(morph css3d-objects :into helix))
    (on-click :grid #(morph css3d-objects :into grid))
    (on-click :reset reset-controls)

    (morph css3d-objects :into table))) 

(defmethod process-msg :elements [[_ elements]]
  (init elements))

(defn send-get-elements []
  (dom/whenever-dom-ready #(ws/send! [:get-elements true])))

(send-get-elements)

;;figwheel calls this function when new code is available
(defn on-js-reload []
  (send-get-elements)) 
