(ns four.transit
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream])
  (:require [cognitect.transit :as transit]))

(defn serialize [val]
  (with-open [output-stream (ByteArrayOutputStream. 4096)]
    (transit/write (mk-writer output-stream) val)
    (.toString output-stream)))

(defn deserialize [a-string]
  (with-open [input-stream (ByteArrayInputStream. (.getBytes a-string))]
    (transit/read (mk-reader input-stream))))

