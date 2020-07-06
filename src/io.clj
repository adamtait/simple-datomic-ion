(ns io
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn edn-resource
  [path]
  (some-> path
          io/resource
          slurp
          edn/read-string))
