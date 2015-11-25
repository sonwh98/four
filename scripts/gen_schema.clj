(ns gen-schema
  (:require [four.server.db :as db]
            [datomic-schema.schema :as s]
            [clojure.pprint :as pp :refer [pprint]]))

(def schema
  [(s/schema element
             (s/fields
              [name :string :indexed]
              [weight :string :indexed]
              [symbol :string :indexed]))])

(pprint (s/generate-schema schema))
