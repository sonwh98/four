(ns four.server
  (:require [datomic.api :as d]
            [org.httpkit.server :as hk]))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})


(defn -main [& args]
  (println "running")
  (hk/run-server app {:port 8080})
  )
