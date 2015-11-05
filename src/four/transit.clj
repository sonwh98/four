(ns four.transit
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream])
  (:require [cognitect.transit :as t]))

(defn serialize [val]
  (with-open [output-stream (ByteArrayOutputStream. 4096)]
    (t/write (t/writer output-stream :json) val)
    (.toString output-stream)))

(defn deserialize [a-string]
  (with-open [input-stream (ByteArrayInputStream. (.getBytes a-string))]
    (t/read (t/reader input-stream :json))))

