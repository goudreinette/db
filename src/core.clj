(ns core
  (:use date set)
  (:refer-clojure :exclude [find])
  (:require [clojure.core.match :refer [match]]
            [hara.time :refer [now before minus adjust from-map]]))

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

(defn exec-event [type {:as db :keys [history state]} attributes attrs]
  (as-> (apply event type attributes attrs) event
    (assoc db
      :history (conj history event)
      :state   (transition state event))))


; Pure API
(defn rewind [history to-date]
  (->> history
    (take-while #(before (:date %) to-date))
    (replay)))

(defn trace [it]
  (println it)
  it)

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


; Persistence
(defn init [file]
  (let [history (->> file slurp read-string (sort-by :date))
        state   (replay history)]
    (atom (->DB file history state))))

(defn save [db]
  (spit (db :file) (db :history)))
