(ns four.client.three
  (:require [cljsjs.three]
            [cljsjs.tween]))

(enable-console-print!)

(def three js/THREE)


(def tween js/TWEEN)
(def Tween (.. tween -Tween))

(defn rotate [mesh]
  (let [x (.. mesh -rotation -x)
        y (.. mesh -rotation -y)]
    (set! (.. mesh -rotation -x)
          (+ x 0.1))
    (set! (.. mesh -rotation -y)
          (+ y 0.1))))

(defn on-js-reload []
  )


