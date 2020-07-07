(ns datomic-ion.api-gateway.app
  (:require [datomic.client.api :as d]
            [datomic.ion.lambda.api-gateway :as api-gateway]
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
  [{:keys [headers body] :as request}]
  {
   :status 200
   :isBase64Encoded false
   
   :headers
   {"Content-Type" "text/plain"}

   :body
   (str
    "datomic-ion.api-gateway.app - schema version: "
    (schema-version))})

(def app
  (api-gateway/ionize handler))
