(ns four.server.db
  (:require [datomic.api :as d]
            [reloaded.repl :refer [system]]
            [clojure.pprint :as pp :refer [pprint]]))


(defn get-conn []
  (:conn  (:datomic-db system)))

(defn get-db []
  (d/db (get-conn)))
