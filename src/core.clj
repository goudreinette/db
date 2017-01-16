(ns core
  (:use date set)
  (:require [clojure.core.match :refer [match]]
            [hara.time :refer [now]]))


(defn event [type attributes & {:keys [where] :as attrs}]
  (merge {:type type
          :attributes attributes
          :date (now)} attrs))


(defrecord DB [file history state])


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


; Persistence
(defn init [file]
  (let [history (->> file slurp read-string (sort-by :date))
        state   (replay history)]
    (atom (->DB file history state))))

(defn save [db]
  (spit (db :file) (db :history)))

(defn exec-event [type db attributes & args]
  (let [event   (event type attributes)
        history (conj (:history db) event)
        state   (transition (:state db) event)]
    (assoc db :history history :state state)))

(defn exec-event! [type db attributes & args]
  (save (apply exec-event type @db attributes args)))
