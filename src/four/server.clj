(ns four.server
  (:require [datomic.api :as d]
            [org.httpkit.server :as s]
            [four.transit :as t]))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})


(defn -main [& args]
  (println "running")
  (let [foo (t/serialize {:a 1})]
    (println foo)
    (println (t/deserialize foo)))
  (s/run-server app {:port 8080})
  )
