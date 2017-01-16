(ns date-test
  (:use date)
  (:require [midje.sweet :refer [facts contains]])
  (:require [hara.time :refer [now minus epoch day]]))


(facts "about date-range"
  (let [week-before-epoch (minus (epoch) {:weeks 1})
        range             (date-range week-before-epoch (epoch) {:days 1})]
   (day (first range)) => 25
   (day (last  range)) => 31
   (count range) => 7))

(facts "about at-or-rewind"
  (day (at-or-rewind :rewind {3 :days} :at nil)) => (- (day (now)) 3)
  (day (at-or-rewind :at     {:day  5}        )) => 5)
