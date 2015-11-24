(ns four.server.main
  (:require [org.httpkit.server :as http-kit]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes]]
            [four.server.ws :as ws]
            [four.server.chemical]
            [four.server.db :as db]
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
    :datomic-db (new-datomic-db (or (env :db-url) "datomic:mem://four"))
    ;:web (new-web-server (or (env :http-port) 8080) handler)
    :repl-server (new-repl-server 2222)))

(defn -main [& args]
  (http-kit/run-server ws/listen-for-client-websocket-connections {:port 9090})
  (http-kit/run-server (site #'all-routes) {:port 8080})
  (reloaded.repl/set-init! dev-system)
  (reloaded.repl/go))
