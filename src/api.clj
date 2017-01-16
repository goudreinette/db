(ns api
  (:use core date set)
  (:require [hara.time :refer [now before minus adjust from-map]]))

; Helpers
(defn rewind [{history :history} to-date]
  (let [history-until-date (take-while #(before (:date %) to-date) history)
        state-at-date      (replay history-until-date)]
    state-at-date))


; API
; (find! db :rewind {4 :hours} :project [:likes] :where {:name "Me"})
(defn find! [db & {a :at r :rewind w :where p :project}]
  (let [date      (absolute-date :rewind r :at a)
        all       (if date (rewind @db date) (:state @db))
        where     (or w {})
        filtered  (select where all)
        projected (if p (map #(select-keys % p) filtered) filtered)]
    projected))


(defn slice! [db & {:keys [from to by where project]}]
  (let [from    (absolute-date :at from)
        to      (absolute-date :at to)
        dates   (date-range from to by)
        dates-i (map format-date dates)
        results (map #(find! db :at % :where where :project project) dates)]
      (map vector dates-i results)))

(def assert!  (partial exec-event! :assert))
(def retract! (partial exec-event! :retract))


; Testing
; (def db (init "db.edn"))
