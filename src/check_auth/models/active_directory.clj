(ns check-auth.models.active-directory
  (:require [check-auth.config :refer [config]]
            [clj-ldap.client :as ad]))

(def server-spec {:host (config :ad-host)
                  :bind-dn (config :ad-bind-dn)
                  :password (config :ad-pwd)
                  :num-connections 10})

(def dn (config :ad-dn))
(def archive-dn (config :ad-archive-dn))

(def ad-pool (ad/connect server-spec))

(defn get-id [id]
  (let [conn (ad/get-connection ad-pool)
        id-rec (ad/get conn (str "cn=" id "," dn))
        id-rec2 (ad/get conn (str "cn=" id "," archive-dn))]
    (ad/release-connection ad-pool conn)
    (or id-rec
        id-rec2)))

(defn get-identities [etype]
  (let [conn (ad/get-connection ad-pool)
        rslt (ad/search conn dn {:filter (str "(employeeType=" etype ")")
                                 :attributes [:uid]
                                 :size-limit 0})]
    (ad/release-connection ad-pool conn)
    (map :uid rslt)))

(defn get-archived-identities [etype]
  (let [conn (ad/get-connection ad-pool)
        rslt (ad/search conn archive-dn {:filter (str "(employeeType=" etype ")")
                                         :attributes [:cn]
                                         :size-limit 0})]
    (ad/release-connection ad-pool conn)
    (map :cn rslt)))
