(ns four.server
  (:require [datomic.api :as d]
            [org.httpkit.server :as s]))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})


(defn -main [& args]
  (println "running")
  (s/run-server app {:port 8080})
  )
