(ns datomic-ion.app
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [datomic.client.api :as d]
            [io :refer [edn-resource]]))

(def datomic-cfg (edn-resource "datomic/config.edn"))

(def get-client
  (memoize
   #(d/client (:client datomic-cfg))))

(def get-conn
  (memoize
   #(d/connect
     (get-client)
     (select-keys datomic-cfg [:db-name]))))

(defn schema-version []
  (ffirst
   (d/q
    '[:find ?n
      :where
      [_ :datomic-ion/schema-version ?n]]
    (d/db (get-conn)))))

(defn handler
  [{:keys [input context]}]
  (str
   "datomic-ion.app - schema version: "
   (schema-version)))
