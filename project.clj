(defproject four "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories  {"my.datomic.com" {:url      "https://my.datomic.com/repo"
                                    :username "adam@tree.io"
                                    :password "5e704e1c-b292-407f-b66c-2d7b677a45d2"}}
  
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.taoensso/timbre "4.1.4"]
                 
                 [org.clojure/clojurescript "1.7.145"]
                 [reagent "0.5.1"]
                 [cljsjs/three "0.0.72-css3d"]
                 [cljsjs/tween "16.3.1"]
                 [krate "0.2.5-SNAPSHOT"]
                 
                 [org.codehaus.groovy/groovy-all "2.4.5"]
                 [com.datomic/datomic-free "0.9.5327" :exclusions [joda-time]]
                 
                 [ring/ring-core "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [compojure "1.4.0"]
                 [http-kit "2.1.19"]
                 
                 [com.cognitect/transit-clj "0.8.285"]
                 [com.cognitect/transit-cljs "0.8.225"]]

  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-figwheel "0.4.1"]
            ]

  :source-paths ["src"]
  :main four.server.main
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  
  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src"]

                        :figwheel { :on-jsload "four.client.three/on-js-reload" }

                        :compiler {:main four.client.elements
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/four.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true}}
                       {:id "min"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/compiled/four.min.js"
                                   :main four.elements
                                   :optimizations :advanced
                                   :pretty-print false}}]}

  :figwheel {
             ;; :http-server-root "public" ;; default and assumes "resources" 
             ;; :four.server-port 3449 ;; default
             ;; :four.server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL four.server into the running figwheel process
             ;; :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; four.server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own four.server :)
             ;; :ring-handler hello_world.four.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :four.server-logfile "tmp/logs/figwheel-logfile.log"
             })
