(ns api
  (:refer-clojure :exclude [find])
  (:require [core :refer :all]))

; Persistence
(defn init [file]
  (let [history (->> file slurp read-string (sort-by :date))
        state   (replay history)]
    (atom (->DB file history state))))

(defn save [{:keys [file history] :as db}]
  (spit file history))


; Base API
(defn exec-event! [type db attributes & args]
  (-> @db
    (exec-event type attributes args)
    (save)))

(defn query! [f db & args]
  (f @db args))

; Derived API
(def assert!     (partial exec-event! :assert))
(def retract!    (partial exec-event! :retract))
(def find!       (partial query!      find))
(def slice!      (partial query!      slice))
(def states!     (partial query!      states))
