(ns gen-schema
  (:require [four.server.db :as db]
            [datomic-schema.schema :as s]
            ))

(def schema
  [(s/schema element
             (s/fields
              [name :string :indexed]
              [weight :string :indexed]
              [symbol :string :indexed]))])

(println (pr-str (s/generate-schema schema)))
