(defproject reinvdwoerd/db "0.0.1"
  :description ""
  :dependencies
    [[org.clojure/clojure "1.8.0"]
     [org.clojure/core.match "0.3.0-alpha4"]
     [org.clojure/core.incubator "0.1.4"]
     [im.chit/hara.time "2.4.8"]
     [reinvdwoerd/sandbox "0.0.1"]]
  :main api
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:source-paths ["test"]
                   :dependencies [[midje "1.8.3"]
                                  [proto-repl "0.3.1"]]}})
