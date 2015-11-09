(ns four.server.main
  (:require [datomic.api :as d]
            [org.httpkit.server :as s]
            [chord.http-kit :refer [with-channel]]
            [clojure.core.async :refer [<! >! put! close! go]]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes]]
            [four.transit :as t]
            [clojure.java.io :as io]))

(defroutes all-routes
  (route/resources "/" ))

(defn websocket-handler [request]
  (with-channel request ws-ch
    (go (let [{:keys [message]} (<! ws-ch)
              elements-edn-file (io/file (io/resource "public/elements.edn"))
              elements-edn (-> elements-edn-file slurp read-string)
              transit-msg (t/serialize elements-edn)]
          (prn "got " message)
          (>! ws-ch transit-msg)))))

(defn -main [& args]
  (println "running")
  (s/run-server websocket-handler {:port 9090})
  (s/run-server (site #'all-routes) {:port 8080}))
