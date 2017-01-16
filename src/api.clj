(ns api
  (:refer-clojure :exclude [find])
  (:require [core :refer [find save exec-event init]]))

; API
(defn exec-event! [type db-atom attributes & args]
  (save (apply exec-event type @db-atom attributes args)))

; (find! db :rewind {4 :hours} :project [:likes] :where {:name "Me"})
(defn find! [db-atom & options]
  (apply core/find @db-atom options))

(defn slice! [db-atom & options]
  (apply core/slice @db-atom options))



(def assert!  (partial exec-event! :assert))
(def retract! (partial exec-event! :retract))


; Testing
(def db (init "db.edn"))
