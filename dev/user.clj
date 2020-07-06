(ns user
  (:require [datomic-ion.app :refer [get-client get-conn]]
            [datomic.client-api :as d]))

(defn get-datomic-config []
  (edn-resource "datomic/config.edn"))

(comment

  (d/create-database
   (get-client)
   (select-keys (get-datomic-config) [:db-name]))
  
  (d/transact
   (get-conn)
   {:tx-data [{:datomic-ion/schema-version 0}]}))
