(ns check-auth.core
  (:require [clojure.string :as s]
            [lanterna.screen :as scr]
            [check-auth.ui.windows :as w]
            [check-auth.ui.formatting :as f]
            [check-auth.ui.input :as i]
            [check-auth.ui.utils :as u]
            [check-auth.config :refer [config]]
            [check-auth.models.open-ldap :as ldap]
            [check-auth.util :as util]
            [clj-time.format :as tf])
   (:import [jcifs.util Hexdump MD4])
  (:gen-class))

(def date-formatter (tf/formatters :date))

(defn hash-nt-password [pwd]
  (let [pwd-bytes (.getBytes pwd "UnicodeLittleUnmarked")
        md4 (doto (MD4.)
              (.engineUpdate pwd-bytes 0 (alength pwd-bytes)))
        hash-bytes (.engineDigest md4)]
    (Hexdump/toHexString hash-bytes 0 (* 2 (alength hash-bytes)))))

(defn get-username [window row]
  (u/command-prompt window "Username" row)
  (w/redraw-window window)
  (i/read-input-line :w1))

(defn get-password [window row]
  (u/command-prompt window "Password" row)
  (w/redraw-window window)
  (i/read-input-line window))

(defn get-id-args [window row]
  (let [id (get-username window row)
        pwd (if-not (or (= ":exit" id)
                        (= "" id))
              (get-password window (inc row))
              nil)]
    {:id id :pwd pwd}))

(defn display-ldap-info [user win]
  (let [last-change-str (tf/unparse date-formatter
                                    (util/last-change-date
                                     (util/parse-int (:shadowLastChange user))))]
    (u/write-text win (str "Username: " (:uid user)) 2 5)
    (u/write-text win (str "Employee Number: " (:employeeNumber user)) 2 6)
    (u/write-text win (str "Display Name: " (:displayName user)) 2 7)
    (u/write-text win (str "Employee Type: " (:employeeType user)) 2 8)
    (u/write-text win (str "Shadow Max: " (:shadowMax user)) 2 9)
    (u/write-text win (str "Shadow Warning: " (:shadowWarning user)) 2 10)
    (u/write-text win (str "Shadow Last Change: " last-change-str) 2 11)))

(defn display-pwd-info [user-rec pwd win]
  (let [nt-pwd (hash-nt-password pwd)
        ldap-nt-pwd (:sambaNTPassword user-rec)]
    (u/write-text win (str "Password " pwd) 2 12)
    (w/redraw-window win)
    (if (= nt-pwd ldap-nt-pwd)
      (u/write-text win (str "Valid NTLM Password: yes") 2 13)
      (do
        (u/write-text win (str "Valid NTLM Password: no") 2 13)
        (u/write-text win (str "LDAP: " ldap-nt-pwd) 2 14)
        (u/write-text win (str "NEW:  " nt-pwd) 2 15)))))

(defn main-window
  "Main UI window"
  []
  (let [w1 (w/make-window :w1 60 20)
        win-cols (w/get-window-columns :w1)
        win-rows (w/get-window-rows :w1)
        title (str (:app-name config) " " (:app-version config) " " (:app-profile config))
        prompt-row (- win-rows 3)]
    (w/create-window :w1)
    (u/write-line :w1 (f/centre-text title win-cols))
    (u/write-line :w1 (f/centre-text "Enter :exit to quit" win-cols))
    (loop [id-info (get-id-args :w1 prompt-row)]
      (println (str "ID-INFO: " id-info))
      (condp = (:id id-info)
        ":exit"  (do
                   (println (str "You entered :exit to quit"))
                   (println "Destroying Window")
                   (w/destroy-window :w1)
                   (println "Exiting"))
        "" (recur (get-id-args :w1 prompt-row))
        (let [[user-rec msg] (ldap/get-id (:id id-info))]
          (u/clear-screen :w1 \space 3)
          (cond
            (and (= msg "OK")
                 user-rec) (do
                             (display-ldap-info user-rec :w1)
                             (display-pwd-info user-rec (:pwd id-info) :w1))
            (and (= msg "OK")
                 (= user-rec nil)) (u/write-text :w1 (str "Identity " (:id id-info) " not in LDAP") 2 5)
            (not (= msg "OK")) (u/write-text :w1 (str "Connection to LDAP failed: " msg) 2 5)
            :default (u/write-text :w1 (str "Unknown error - contact administrator")))
          (recur (get-id-args :w1 prompt-row)))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Application: " (:app-name config))
  (println "Version:     " (:app-version config))
  (println "Profile:     " (:app-profile config)))
