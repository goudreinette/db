(ns api
  (:refer-clojure :exclude [find])
  (:require [core :refer :all]))

; Persistence
(defn init [file]
  (let [history (->> file slurp read-string (sort-by :date))
        state   (replay history)]
    (agent (->DB file history state))))

(defn save [db]
  (spit (db :file) (db :history)))


; Base API
(defn exec-event! [db & args]
  (apply send db exec-event args))

(defn query! [f db & args]
  (apply f @db args))

; Derived API
(def assert!     (partial exec-event! :assert))
(def retract!    (partial exec-event! :retract))
(def find!       (partial query!      find))
(def slice!      (partial query!      slice))


; Test
; Example:
;   (find! db :rewind {4 :hours} :project [:likes] :where {:name "Me"})
(def db (init "db.edn"))
