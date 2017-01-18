(ns set
  (:require [clojure.set :as set]))

(defn project [ks all]
  (cond-> all
   ks (set/project ks)))

(defn match? [where entity]
  (= (select-keys entity (keys where)) where))

(defn select [where all]
  (cond->> all
    where (set/select #(match? where %))))

(defn update-matching [f where all]
  (map #(if (match? where %) (f %) %)
       all))




(defn insert [entity all]
  (conj all entity))

(defn update-where [where attrs all]
  (update-matching #(merge % attrs) where all))

(defn remove-attrs-where [where keys all]
  (update-matching #(apply dissoc % keys) where all))

(defn remove-where [where all]
  (set/select #(not (match? where %)) all))

(def remove-attrs-everywhere (partial remove-attrs-where {}))
