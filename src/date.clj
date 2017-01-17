(ns date
  (:refer-clojure :exclude [format])
  (:require [hara.time :refer [now to-map default-type plus minus before adjust format coerce]]
            [clojure.set :refer [map-invert]]))

(default-type java.util.Date)


(defn format-date [date]
  (format date "E dd-MM-yyy HH:ss"))

(defn date-range [from to by]
  (take-while #(before % to)
               (iterate #(plus % by) from)))

(defn at-or-rewind [& {:keys [rewind at]}]
  (cond
    at     (adjust (now) at)
    rewind (minus  (now) (map-invert rewind))
    :else  (now)))
