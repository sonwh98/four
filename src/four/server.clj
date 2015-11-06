(ns four.server
  (:require [datomic.api :as d]
            [org.httpkit.server :as s]
            [four.transit :as t]))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})


(defn handler [request]
  (s/with-channel request channel
    (s/on-close channel (fn [status] (println "channel closed: " status)))

    (s/on-receive channel (fn [data] ;; echo it back
                            (s/send! channel (t/serialize {:first "Connor" :last "To"}))))))

(defn -main [& args]
  (println "running")
  (s/run-server handler {:port 9090})
                                        ;(s/run-server app {:port 8080})
  )
