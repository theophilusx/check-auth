(defproject check-auth "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [jcifs/jcifs "1.3.18"]
                 [org.clojars.pntblnk/clj-ldap "0.0.12"]
                 [cprop "0.1.10"]
                 [org.clojure/tools.cli "0.3.5"]]
  :main ^:skip-aot check-auth.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
