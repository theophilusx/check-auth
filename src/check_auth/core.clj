(ns check-auth.core
  (:require [clojure.string :as s]
            [lanterna.screen :as scr]
            [check-auth.ui.windows :as w]
            [check-auth.ui.formatting :as f]
            [check-auth.ui.input :as i]
            [check-auth.ui.utils :as u])
  (:gen-class))


(defn get-username [window row]
  (u/command-prompt window "Username" row)
  (w/redraw-window window)
  (i/read-input-line :w1))

(defn get-password [window row]
  (u/command-prompt window "Password" row)
  (w/redraw-window window)
  (i/read-input-line window))

(defn main-window
  "Main UI window"
  []
  (let [w1 (w/make-window :w1 60 20)
        win-cols (w/get-window-columns :w1)
        win-rows (w/get-window-rows :w1)]
    (w/create-window :w1)
    (u/write-line :w1 (f/centre-text "Authentication Check" win-cols))
    (u/write-line :w1 (f/centre-text "--------------------" win-cols))
    (u/write-line :w1 (f/centre-text "Enter :exit to quit" win-cols))
    (loop [input (get-username :w1 (- win-rows 2))]
      (when-not (= ":exit" input)
        (println (str "You entered: " input))
        (u/write-text :w1 (str "Username " input) 2 5)
        (let [pwd (get-password :w1 (- win-rows 2))]
          (u/write-text :w1 (str "Password " pwd) 2 6)
          (w/redraw-window :w1))
        (recur (get-username :w1 (- win-rows 2)))))
    (println (str "You entered :exit to quit"))
    (println "Destroying Window")
    (w/destroy-window :w1)
    (println "Exiting")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  )
