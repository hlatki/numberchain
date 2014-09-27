(defproject number-chain "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2342"]
                 [figwheel "0.1.3-SNAPSHOT"]]

  :jvm-opts ["-Xmx1G"]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-figwheel "0.1.3-SNAPSHOT"]
            [com.cemerick/austin "0.1.4"]
            [com.cemerick/clojurescript.test "0.3.1"]]

  :figwheel {
    :http-server-root "public"
    :port 3449
    :css-dirs ["resources/public/css"]}

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src/number_chain" "src/figwheel" "src/brepl"]
              :compiler {
                :output-to "resources/public/number_chain.js"
                :output-dir "resources/public/out"
                :optimizations :none
                :source-map true}}
             {:id "release"
              :source-paths ["src/number_chain"]
              :compiler {
                :output-to "resources/public/number_chain_prod.js"
                :output-dir "resources/public/prod-out"
                :optimizations :advanced
                :pretty-print false
                :source-map "resources/public/number_chain_prod.js.map"}}
             {:id "test"
              :source-paths ["src/number_chain" "test"]
              :notify-command ["phantomjs" :cljs.test/runner "number_chain_test.js"]
              :compiler {
                :output-to "number_chain_test.js"
                :optimizations :whitespace}}]})
