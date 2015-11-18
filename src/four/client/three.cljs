(ns four.client.three
  (:require [cljsjs.three]
            [cljsjs.tween]))

(enable-console-print!)

(def three js/THREE)
(def Scene (.-Scene three))

(def PerspectiveCamera (.-PerspectiveCamera three))
(def WebGLRenderer (.-WebGLRenderer three))
(def BoxGeometry (.-BoxGeometry three))
(def MeshBasicMaterial (.-MeshBasicMaterial three))
(def Mesh (.-Mesh three))

(def CSS3DObject (.-CSS3DObject three))
(def Object3D (.-Object3D three))
(def CSS3DRenderer (.-CSS3DRenderer three))
(def TrackballControls (..  three -TrackballControls))
(def Vector3 (.. three -Vector3))

(def tween js/TWEEN)
(def Tween (.. tween -Tween))

(defn rotate [mesh]
  (let [x (.. mesh -rotation -x)
        y (.. mesh -rotation -y)]
    (set! (.. mesh -rotation -x)
          (+ x 0.1))
    (set! (.. mesh -rotation -y)
          (+ y 0.1))))

(defn animate [animation-fn]
  ((fn animation-loop [time]
     (animation-fn time)
     (js/requestAnimationFrame animation-loop))))

(defn on-js-reload []
  )

(defprotocol IShape
  (add [this element])
  (to-seq [this]))