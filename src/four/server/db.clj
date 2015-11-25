(ns four.server.db
  (:require [datomic.api :as d]
            [reloaded.repl :refer [system]]
            ))


(defn get-conn []
  (:conn  (:datomic-db system)))

(defn get-db []
  (d/db (get-conn)))

(defn transact [datoms]
  (d/transact (get-conn) datoms))
