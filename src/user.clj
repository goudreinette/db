(ns user
  (use api))

; Example:
;   (find! db :rewind {4 :hours} :project [:likes] :where {:name "Me"})

(def db (init "db.edn"))
