(ns api.monolith.dev
  (:require [datomic.client.api :as d]))

(def db-name "dev")
(def dcfg
  {:server-type :ion
   :creds-profile "<if-you-have-one>"
   :region "<region>"
   :system "<system-name>"
   :query-group "<system-name>"
   :endpoint "http://entry.<system-name>.<region>.datomic.net:8182/"
   :proxy-port 8182})

(def get-client
  (memoize #(d/client dcfg)))
(def get-conn (memoize #(d/connect (get-client) {:db-name db-name})))

(defn schema-version []
  (ffirst
   (d/q
    '[:find ?n
      :where
      [_ ::schema-version ?n]]
    (d/db (get-conn)))))

(defn app
  [{:keys [input context]}]
  (str
   "api monolith dev - schema version: "
   (schema-version)))



(comment

  (d/create-database (get-client) {:db-name db-name})
  (d/transact (get-conn) {:tx-data [{::schema-version 0}]}))
