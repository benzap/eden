(defproject eden "0.1.0-SNAPSHOT"
  :description "lua-based scripting language in Clojure(script)"
  :url "http://github.com/benzap/eden"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [org.clojure/tools.reader "1.2.2"]
                 [org.clojure/tools.cli "0.3.7"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-ancient "0.6.15"]
            [lein-doo "0.1.10"]])
