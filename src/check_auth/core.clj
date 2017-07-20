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
  (:gen-class))

(def date-formatter (tf/formatters :date))

(defn get-username [window row]
  (u/command-prompt window "Username" row)
  (w/redraw-window window)
  (i/read-input-line :w1))

(defn get-password [window row]
  (u/command-prompt window "Password" row)
  (w/redraw-window window)
  (i/read-input-line window))

(defn display-ldap-info [uid win]
  (if-let [user (ldap/get-id uid)]
    (let [last-change-str (tf/unparse date-formatter
                                     (util/last-change-date
                                      (util/parse-int (:shadowLastChange user))))]
      (u/write-text win (str "Username: " (:uid user)) 2 5)
      (u/write-text win (str "Employee Number: " (:employeeNumber user)) 2 6)
      (u/write-text win (str "Display Name: " (:displayName user)) 2 7)
      (u/write-text win (str "Employee Type: " (:employeeType user)) 2 8)
      (u/write-text win (str "Shadow Max: " (:shadowMax user)) 2 9)
      (u/write-text win (str "Shadow Warning: " (:shadowWarning user)) 2 10)
      (u/write-text win (str "Shadow Last Change: " last-change-str) 2 11)
      true)
    (do
      (u/write-text win (str "Identity " uid " not in LDAP") 2 5)
      false)))

(defn display-pwd-info [uid win]
  (let [pwd (get-password :w1 (- (w/get-window-rows win) 2))]
            (u/write-text :w1 (str "Password " pwd) 2 6)
            (w/redraw-window :w1)))

(defn main-window
  "Main UI window"
  []
  (let [w1 (w/make-window :w1 60 20)
        win-cols (w/get-window-columns :w1)
        win-rows (w/get-window-rows :w1)
        title (str (:app-name config) " " (:app-version config) " " (:app-profile config))]
    (w/create-window :w1)
    (u/write-line :w1 (f/centre-text title win-cols))
    (u/write-line :w1 (f/centre-text "Enter :exit to quit" win-cols))
    (loop [username (get-username :w1 (- win-rows 2))]
      (condp = username

        ":exit"  (do
                   (println (str "You entered :exit to quit"))
                   (println "Destroying Window")
                   (w/destroy-window :w1)
                   (println "Exiting"))
        "" (recur (get-username :w1 (- win-rows 2)))
        (do
          (when (display-ldap-info username :w1)
            (display-pwd-info username :w1))
          (recur (get-username :w1 (- win-rows 2))))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Application: " (:app-name config))
  (println "Version:     " (:app-version config))
  (println "Profile:     " (:app-profile config)))
