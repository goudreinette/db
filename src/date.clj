(ns date
  (:refer-clojure :exclude [format])
  (:require [hara.time :refer [now to-map default-type plus minus before adjust format coerce]]
            [clojure.set :refer [map-invert]]
            [akar.syntax :refer [match]]))

(default-type java.util.Date)


(defn date-range [from to by]
  (take-while #(before % to)
               (iterate #(plus % by) from)))

(defn at-or-rewind [& {:as args}]
  (match args
    {:at at}         (adjust (now) at)
    {:rewind rewind} (minus  (now) (map-invert rewind))
    :_               (now)))
