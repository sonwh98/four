(ns four.client.menu
  (:require [four.client.core :as four :refer [morph div->css3d-object]]
            [four.client.dom :as dom]
            [four.client.layout :as layout]
            [four.client.ws :as ws :refer [process-msg]]
            [four.messaging :as m]
            [crate.core :as c]))


(declare scene)
(declare renderer)
(declare camera)

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

(defn init []
  (def scene (js/THREE.Scene.))
  (let [fov (radian->degree (calculate-fov (.-innerHeight dom/window)
                                           1000))]
    (def camera (js/THREE.PerspectiveCamera. fov
                                             (/ (.-innerWidth dom/window)
                                                (.-innerHeight dom/window))
                                             1000
                                             1)))
  (set! (.. camera -position -z) 1000)
  
  (def renderer (js/THREE.CSS3DRenderer.))
  (. renderer (setSize (. dom/window -innerWidth)
                       (. dom/window -innerHeight)))
  (def domElement (. renderer -domElement))
  (set! (.. domElement -style -position) "absolute")
  (. (dom/by-id "container") appendChild domElement)

  (letfn [(render-scene []
                        (. renderer (render scene camera)))]
    (four/animate (fn [time]
                    (.. js/TWEEN update)
                    (render-scene)))))

(init)

(defn on-click [button-id callback-fn]
  (println button-id " " (dom/by-id button-id))
  (dom/whenever-dom-ready #(dom/on (dom/by-id button-id) "click" callback-fn)))

(defn build-scene [catalog]
  (let [id->css3dobj (atom {})
        active-category-button (atom nil)
        active-category-container (atom nil)
        category-button-container-div [:div {:id "category-menu"}
                                       (for [category catalog
                                             :let [cat-name (:category/name category)]]
                                         [:button {:id cat-name} cat-name])]
        category-button-container-css3d-object (div->css3d-object (c/html category-button-container-div))
        category-buttons (array-seq (.. category-button-container-css3d-object -element (querySelectorAll "button")))
        off-screen-left [{:x (- js/window.innerWidth) :y 0 :z 0}]
        left-align-x [{:x (/ js/window.innerWidth -2) :y 0 :z 0}]
        categories  (doall  (for [category catalog
                                  :let [color (-> (* (rand) 0.5) (+ 0.25))
                                        products (:products category)
                                        id (str "category-" (:category/name category))

                                        div [:div {:id    id
                                                   :class "category"
                                                   :style {:backgroundColor (str "rgb(0,127,127)")
                                                           :border-style "solid"
                                                           :border-color "white"
                                                           :width "40%"}}
                                             (for [p products]
                                               [:button {:id (:product/sku p)
                                                         :class "product"}
                                                [:img {:src (or (:url p) "http://www.creattor.com/files/10/652/drinks-icons-screenshots-1.png")
                                                       :class "product-img"}]
                                                [:div  (:product/name p)]])]
                                        css3d-object (div->css3d-object (c/html div))]]
                              (do
                                (swap! id->css3dobj assoc id css3d-object)
                                (.. scene (add css3d-object))
                                css3d-object)))
        get-category-container (fn [category-button]
                                 (let [category-name (. category-button -id)]
                                   (@id->css3dobj (str "category-" category-name))))

        set-active-category-container (fn [category-button]
                                        (let [category-container-css3dobj (get-category-container category-button)
                                              category-container-div (. category-container-css3dobj -element)
                                              category-container-width (. category-container-div -clientWidth)
                                              category-container-height (. category-container-div -clientHeight)
                                              
                                              ]
                                          (if (nil? @active-category-button)
                                            (do
                                              (reset! active-category-button category-button)
                                              (reset! active-category-container category-container-css3dobj)))
                                          
                                          (set! (.. @active-category-button -style -backgroundColor) nil)
                                          (morph [@active-category-container] :into off-screen-left)

                                          (reset! active-category-button category-button)
                                          (reset! active-category-container category-container-css3dobj)
                                          (set! (.. @active-category-button -style -backgroundColor) "rgb(100,100,100)")
                                          (morph [category-container-css3dobj] :into left-align-x)
                                          ))]
    
    (.. scene (add category-button-container-css3d-object))
    (morph [category-button-container-css3d-object] :into [{:x 0
                                                            :y 0
                                                            :z 0}])
    (def id->css3dobj id->css3dobj)
    (set-active-category-container (first category-buttons))
    (doseq [category-button category-buttons]
      (dom/on category-button "click" #(set-active-category-container category-button)))))

(defmethod process-msg :catalog [[_ catalog]]
  (build-scene catalog))

(defn send-get-catalog []
  (dom/whenever-dom-ready #(ws/send! [:get-catalog true])))

(defn on-js-reload [])

(send-get-catalog)
