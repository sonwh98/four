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

(defn init []
  (def scene (js/THREE.Scene.))
  (def camera (js/THREE.PerspectiveCamera. 60 (/ (.-innerWidth dom/window)
                                                 (.-innerHeight dom/window))
                                           1000
                                           1))
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

(defn populate [scene _ catalog]
  (let [id->css3dobj (atom {})
        active-category-container (atom nil)]
    (let [category-button-container-div [:div {:id "category-menu"}
                                         (for [category catalog
                                               :let [cat-name (:category/name category)]]
                                           [:button {:id cat-name} cat-name])]
          category-button-container-css3d-object (div->css3d-object (c/html category-button-container-div))
          category-buttons (array-seq (.. category-button-container-css3d-object -element (querySelectorAll "button")))
          left-panel [(four/position-map->object3d {:x -615
                                                    :y 0
                                                    :z 0})]
          center [(four/position-map->object3d {:x -400
                                                :y 0
                                                :z 0})]
          get-category-container (fn [category-name]
                                   (@id->css3dobj (str "category-" category-name)))]
      (.. scene (add category-button-container-css3d-object))
      (morph [category-button-container-css3d-object] :into left-panel)

      (reset! active-category-container (let [category-button (first category-buttons)
                                              category-name (. category-button -id)]
                                          (get-category-container category-name)))
      
      (doseq [category-button category-buttons]
        (dom/on category-button "click" #(let [category-name (. category-button -id)
                                               category-container-css3dobj (get-category-container category-name)
                                               ]
                                           (morph [category-container-css3dobj] :into center)))))


    (let [categories  (doall  (for [category catalog
                                    :let [color (-> (* (rand) 0.5) (+ 0.25))
                                          products (:products category)
                                          id (str "category-" (:category/name category))
                                          div [:div {:id    id
                                                     :class "category"
                                                     :style {:backgroundColor (str "rgb(0,127,127)")
                                                             :border-style "solid"
                                                             :border-color "white"
                                                             :width "300px"}}
                                               (for [p products]
                                                 [:button {:class "product"}
                                                  [:img {:src (or (:url p) "http://www.creattor.com/files/10/652/drinks-icons-screenshots-1.png")
                                                         :class "product-img"}]
                                                  [:div  (:product/name p)]])]
                                          css3d-object (div->css3d-object (c/html div))]]
                                (do
                                  (swap! id->css3dobj assoc id css3d-object)
                                  (.. scene (add css3d-object))
                                  css3d-object)))]
      (def categories categories)))
  scene)

(defn reset-camera []
                                        ;(. controls reset)
  )



(defn build-scene [catalog]
  (populate scene :with catalog)
  (let [css3d-objects (seq (. scene -children))
        size (count css3d-objects)
        table (layout/create-table)
        sphere (layout/create-sphere size)
        helix (layout/create-helix size)
        grid (layout/create-grid size)
        ]
    
    (on-click "sphere" #(morph css3d-objects :into sphere))
    (on-click "helix" #(morph css3d-objects :into helix))
    (on-click "grid" #(morph css3d-objects :into grid))
    (on-click "reset" reset-camera)
    

    )
  )

(defmethod process-msg :catalog [[_ catalog]]
  (build-scene catalog))

(defn send-get-catalog []
  (println "send-get-catalog")
  (dom/whenever-dom-ready #(ws/send! [:get-catalog true])))

(defn on-js-reload [])

(send-get-catalog)
