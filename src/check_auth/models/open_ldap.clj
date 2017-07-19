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
  (let [conn (ldap/get-connection ldap-pool)
        id-rec (ldap/get conn (str "uid=" id "," dn))]
    (ldap/release-connection ldap-pool conn)
    id-rec))

(defn get-identities [etype]
  (let [conn (ldap/get-connection ldap-pool)
        rslt (ldap/search conn dn {:filter (str "(employeeType=" etype ")")
                              :attributes [:uid]
                              :size-limit 0})]
    (ldap/release-connection ldap-pool conn)
    (map :uid rslt)))
