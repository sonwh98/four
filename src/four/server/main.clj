(ns four.server.main
  (:require [datomic.api :as d]
            [org.httpkit.server :as s]
            [chord.http-kit :refer [with-channel]]
            [clojure.core.async :refer [<! >! put! close! go go-loop]]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes]]
            [four.transit :as t]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            (system.components
             [http-kit :refer [new-web-server]]
             [datomic :refer [new-datomic-db]]
             [repl-server :refer [new-repl-server]])
            [reloaded.repl]))

(def client-channels (atom []))

(defroutes all-routes
  (route/resources "/" ))

(defn clean-up! [ws-chanel]
  (println "clean-up " ws-chanel)
  (close! ws-chanel))

(defmulti process-msg (fn [[ch [kw msg]]]
                        kw))

(defmethod process-msg :get-elements [[ws-channel [kw msg]]]
  (let [elements-edn-file (io/file (io/resource "public/elements.edn"))
        elements-edn (-> elements-edn-file slurp read-string)
        transit-msg (t/serialize elements-edn)]
    (go (>! ws-channel transit-msg))))

(defn process-messages! [ws-channel]
  (go-loop []
    (if-let [{:keys  [message]} (<! ws-channel)]
      (do
        (process-msg [ws-channel message])
        (recur))
      (clean-up! ws-channel))))

(defn websocket-handler [request]
  (with-channel request ws-ch
    (process-messages! ws-ch)))

(defn dev-system []
  (component/system-map
   ;:datomic-db (new-datomic-db (env :db-url))
   ;:web (new-web-server (or (env :http-port) 8080) handler)
   :repl-server (new-repl-server 2222)))

(defn -main [& args]
  (println "running")
  (s/run-server websocket-handler {:port 9090})
  (s/run-server (site #'all-routes) {:port 8080})
  (reloaded.repl/set-init! dev-system)
  (reloaded.repl/go))
