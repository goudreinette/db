(ns date-test
  (:use date)
  (:require [midje.sweet :refer [facts contains]])
  (:require [hara.time :refer [now minus epoch day]]))


(facts "about date-range"
  (let [week-before-epoch (minus (epoch) {:weeks 1})
        range             (date-range week-before-epoch (epoch) {:days 1})]
   (first range) => (contains {:day 25})
   (last  range) => (contains {:day 31})
   (count range) => 7))

(facts "about absolute-date"
  ((absolute-date :rewind {3 :days} :at nil) :day) => (- (day (now)) 3)
  ((absolute-date :at     {:day  5}        ) :day) => 5)
