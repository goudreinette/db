(ns core
  (:use date set)
  (:refer-clojure :exclude [find])
  (:require [joy.macros :refer [cond-apply]]
            [akar.syntax :refer [match]]
            [hara.time :refer [now before after minus adjust from-map]]))

(defrecord DB [file history state])

(defn event [type attributes & {:as args}]
  (merge {:type type
          :attributes attributes
          :date (now)} args))


; Event Execution
(defn transition [all event]
  (set
    (match event
      {:type :assert  :attributes a :where w} (update-where w a all)
      {:type :assert  :attributes a         } (insert a all)
      {:type :retract :attributes a :where w} (remove-attrs-where w a all)
      {:type :retract :attributes a         } (remove-attrs-everywhere a all)
      {:type :retract               :where w} (remove-where w all))))


; Takes history
(defn replay-with [f]
  (fn [history] (f transition #{} history)))

(def replay            (replay-with reduce))
(def replay-reductions (replay-with reductions))


(defn take-until-date [history to-date]
  (take-while #(before (:date %) to-date) history))

(defn rewind [history to-date]
  (-> history
    (take-until-date to-date)
    (replay)))

; Takes a DB map
(defn exec-event [{:as db :keys [history state]} type attributes args]
  (as-> (apply event type attributes args) event
    (assoc db
      :history (conj history event)
      :state   (transition state event))))

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

(defn states [{history :history} {to :to}]
  (let [history (take-until-date history (or to (now)))]
    (interleave (map :date history)
                (replay-reductions history))))
