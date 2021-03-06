(defproject eden "0.9.0"
  ;;:global-vars {*warn-on-reflection* true}
  :description "lua-based scripting language in Clojure(script)"
  :url "http://github.com/benzap/eden"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/core.async "0.5.527"]
                 [org.clojure/tools.reader "1.3.2"]
                 [org.clojure/tools.cli "0.4.1"]
                 
                 ;; Eden Contrib Standard Libraries
                 [cheshire "5.8.1"] ;; JSON Library
                 [org.martinklepsch/clj-http-lite "0.4.1"] ;; HTTP Client Library
                 [com.cognitect/transit-clj "0.8.313"] ;; Transit Data Format
                 [funcool/cuerdas "2.1.0"] ;; String Library
                 [com.rpl/specter "1.1.2"] ;; Code Navigation
                 [hiccup "1.0.5"] ;; HTML Generation
                 [hickory "0.7.1"] ;; HTML Parsing
                 [markdown-clj "1.10.0"] ;; Markdown Common Parser
                 [me.raynes/conch "0.8.0"] ;; Shell Program Execution
                 [me.raynes/fs "1.4.6"] ;; More sane file system functions in clojure
                 [http-kit "2.3.0"] ;; HTTP Server Library
                 [ring/ring-defaults "0.3.2"] ;; HTTP Ring Middleware
                 [bidi "2.1.5"] ;; HTTP Routing Library
                 [garden "1.3.6"] ;; CSS Generation
                 [org.clojure/java.jdbc "0.7.9"] ;; Java JDBC for database interaction
                 [org.xerial/sqlite-jdbc "3.27.2"]] ;; Sqlite Driver
  
  
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-ancient "0.6.15"]
            [lein-doo "0.1.10"]]

  :repositories [["clojars" {:sign-releases false}]]

  :cljsbuild {:builds {:dev
                       {:source-paths ["src"]
                        :compiler {:output-dir "resources/public/js/compiled/out"
                                   :output-to "resources/public/js/compiled/eden.js"
                                   :optimizations :whitespace
                                   :pretty-print true
                                   :source-map "resources/public/js/compiled/eden.js.map"}}
                       :prod
                       {:source-paths ["src"]
                        :compiler {:output-to "resources/public/js/compiled/eden.min.js"
                                   :optimizations :advanced
                                   :pretty-print false}}
                       :test
                       {:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "resources/public/js/compiled/test/test-runner.js"
                                   :output-dir "resources/public/js/compiled/test/out"
                                   :main eden.test-runner
                                   :target :nodejs
                                   :optimizations :none}}}}

  :doo {:build "test"
        :alias {:default [:node]}}

  :aliases {"project-version" ["run" "-m" "eden.utils.version/print-project-version"]}

  :profiles
  {:dev
   {:main eden.commandline
    :source-paths ["src" "dev" "test"]
    :dependencies [[org.clojure/tools.namespace "0.2.11"]]
    :repl-options {:init-ns eden.dev.user
                   ;;:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                   :port 9006}}
   
   :uberjar
   {:jvm-opts ["-Dclojure.compiler.direct-linking=true"]
    :main eden.commandline
    :aot [eden.core eden.commandline]}})
    
