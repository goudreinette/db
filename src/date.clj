(ns date
  (:refer-clojure :exclude [format])
  (:require [hara.time :refer [now to-map plus minus before adjust format coerce]]
            [clojure.set :refer [map-invert]]))

(defn as-date [date-like]
  (coerce date-like {:type java.util.Date}))

(defn format-date [date]
  (format date "E dd-MM-yyy HH:ss"))

(defn date-range [from to by]
  (take-while #(before % to)
               (iterate #(as-date (plus % by)) from)))

(defn at-or-rewind [& {:keys [rewind at]}]
  (as-date
    (cond
      at     (adjust (now) at {:type java.util.Date})
      rewind (minus  (now) (map-invert rewind) {:type java.util.Date}))))
