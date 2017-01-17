(ns core
  (:use date set)
  (:refer-clojure :exclude [find])
  (:require [clojure.core.match :refer [match]]
            [hara.time :refer [now before after minus adjust from-map]]))

(defrecord DB [file history state])
(defrecord Event [type attributes date])

(defn event [type attributes & {:as attrs}]
  (merge (->Event type attributes (now)) attrs))


; Event Execution
(defn transition [all event]
  (set
    (match [event]
      [{:type :assert  :attributes a :where w }] (update-where w a all)
      [{:type :assert  :attributes a          }] (insert a all)
      [{:type :retract :attributes a :where w }] (remove-attrs-where w a all)
      [{:type :retract :attributes a          }] (remove-attrs-everywhere a all) ; or all?
      [{:type :retract               :where w }] (remove-where w all))))


(defn replay [history]
  (reduce transition #{} history))

(defn replay-reductions [history]
  (reductions transition #{} history))

(defn exec-event [type {:as db :keys [history state]} attributes attrs]
  (as-> (apply event type attributes attrs) event
    (assoc db
      :history (conj history event)
      :state   (transition state event))))

(defn history-until-date [history to-date]
  (take-while #(before (:date %) to-date) history))

; Pure API


(defn rewind [history to-date]
  (-> history
    (history-until-date to-date)
    (replay)))


(defn find [{h :history} {a :at r :rewind w :where p :project}]
  (->>
    (at-or-rewind :rewind r :at a)
    (rewind h)
    (select w)
    (project p)))

(defn slice [db & {:keys [from to by where project :as options]}]
  (let [dates   (date-range from to by)
        results (map #(find db (assoc options :at %) dates))]
    (map vector dates results)))

(defn states [history to-date]
  (let [history (history-until-date history to-date)]
    (interleave (map :date history)
                (replay-reductions history))))


; Persistence
(defn init [file]
  (let [history (->> file slurp read-string (sort-by :date))
        state   (replay history)]
    (atom (->DB file history state))))

(defn save [db]
  (spit (db :file) (db :history)))

; Test
(def db (init "db.edn"))
