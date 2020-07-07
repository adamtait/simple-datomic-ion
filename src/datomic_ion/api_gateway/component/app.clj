(ns datomic-ion.api-gateway.component.app
  (:require [com.stuartsierra.component :as component]
            [datomic.client.api :as d]
            [datomic.ion.lambda.api-gateway :as api-gateway]
            [io :refer [edn-resource]]))

(def datomic-cfg (edn-resource "datomic/config.edn"))


;; component

(defrecord DatomicComponent [config]
  component/Lifecycle

  (start [this]
    (let [client (d/client (:client config))
          conn-fn #(d/connect
                    client
                    (select-keys datomic-cfg [:db-name]))]
      (assoc
       this
       :client client
       :conn-fn conn-fn)))

  (stop [this]
    (dissoc this :client :conn-fn)))


;; component system

(defn new-system [config]
  (component/system-map
   :datomic (map->DatomicComponent {:config (:datomic config)})))

(def ^:dynamic *system* nil)

(defn get-system []
  (when-not *system*
    (alter-var-root
     #'*system*
     (constantly
      (component/start
       (new-system {:datomic datomic-cfg})))))
  *system*)


;; handler

(defn schema-version [system]
  (let [conn ((get-in system [:datomic :conn-fn]))
        db   (d/db conn)]
    (ffirst
     (d/q
      '[:find ?n
        :where
        [_ :datomic-ion/schema-version ?n]]))))

(defn handler []
  (fn [{:keys [headers body] :as request}]
    (let [system (get-system)]
      {
       :status 200
       :isBase64Encoded false

       :headers
       {"Content-Type" "text/plain"}

       :body
       (str
        "datomic-ion.api-gateway.component.app - schema version: "
        (schema-version system))})))


;; api-gateway app

(def app
  (api-gateway/ionize handler))
