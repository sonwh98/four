(ns four.server.main
  (:require [datomic.api :as d]
            [org.httpkit.server :as s]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes]]
            [four.transit :as t]
            [clojure.java.io :as io]))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})

(defroutes all-routes
  (route/resources "/" )
  )

(defn handler [request]
  (s/with-channel request channel
    (s/on-close channel (fn [status] (println "channel closed: " status)))

    (s/on-receive channel (fn [data]
                            (let [elements-edn-file (io/file (io/resource "public/elements.edn"))
                                  elements-edn (-> elements-edn-file slurp read-string)]
                              (s/send! channel (t/serialize elements-edn)))))))

(defn -main [& args]
  (println "running")
  (s/run-server handler {:port 9090})
  (s/run-server (site #'all-routes) {:port 8080}))
