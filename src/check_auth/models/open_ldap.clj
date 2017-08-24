(ns check-auth.models.open-ldap
  (:require [check-auth.config :refer [config]]
            [clj-ldap.client :as ldap]))

(def server-spec {:host (config :ldap-host)
                  :bind-dn (config :ldap-bind-dn)
                  :password (config :ldap-pwd)
                  :num-connections 10})

(def dn (config :ldap-dn))

(def ldap-pool (ldap/connect server-spec))

(defn get-id [id]
  (try
    (let [conn (ldap/get-connection ldap-pool)
          id-rec (ldap/get conn (str "uid=" id "," dn))]
      (ldap/release-connection ldap-pool conn)
      [id-rec "OK"])
    (catch Exception e
      [nil (str "LDAP Error: " (.getMessage e) " for ID " id)])))

(defn check-auth [id pwd]
  (try 
    (let [conn      (ldap/get-connection ldap-pool)
          bind-status (ldap/bind? conn (str "uid=" id "," dn) pwd)]
      (ldap/release-connection ldap-pool conn)
      [bind-status "OK"])
    (catch Exception e
      [nil (str "LDAP Error: " (.getMessage e) " from ID " id " and PWD " pwd)])))
