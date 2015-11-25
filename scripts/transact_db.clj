(ns transact-db
  (:require [four.server.db :as db]
            [datomic.api :as d]
            [clojure.string :refer [join]]
            [reloaded.repl]
            [four.server.main :as main]
            [environ.core :refer [env]]))

(reloaded.repl/set-init! main/dev-system)
(reloaded.repl/go)

(def db-url (env :db-url))
(println "db-url=" db-url)
(def lines (join "\n" (line-seq (java.io.BufferedReader. *in*))))

(def schema  (read-string lines))
(d/create-database db-url)
(def tx  (db/transact schema))
@tx
(println "tx done")




