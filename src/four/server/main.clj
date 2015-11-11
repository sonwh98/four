(ns four.server.main
  (:require [datomic.api :as d]
            [org.httpkit.server :as s]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes]]
            [four.server.ws :as ws]
            [four.server.chemical]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            (system.components
              [http-kit :refer [new-web-server]]
              [datomic :refer [new-datomic-db]]
              [repl-server :refer [new-repl-server]])
            [reloaded.repl]))

(defroutes all-routes
           (route/resources "/"))

(defn dev-system []
  (component/system-map
    ;:datomic-db (new-datomic-db (env :db-url))
    ;:web (new-web-server (or (env :http-port) 8080) handler)
    :repl-server (new-repl-server 2222)))

(defn -main [& args]
  (println "running")
  (s/run-server ws/websocket-handler {:port 9090})
  (s/run-server (site #'all-routes) {:port 8080})
  (reloaded.repl/set-init! dev-system)
  (reloaded.repl/go))
