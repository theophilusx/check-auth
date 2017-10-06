(ns check-auth.util
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]))

(defn parse-int [int-str]
  (try
    (Integer/parseInt int-str)
    (catch Exception e
      0)))

(defn last-change-date [days-since-epoch]
  (let [epoch (c/to-long (t/epoch))
        last-change (* 1000 60 60 24 days-since-epoch)]
    (c/from-long (+ epoch last-change))))



