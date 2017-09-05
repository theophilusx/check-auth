(defproject check-auth "0.1.0-SNAPSHOT"
  :description "Check LDAP and Active Directory Authentication Values"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [jcifs/jcifs "1.3.18"]
                 [org.clojars.pntblnk/clj-ldap "0.0.12"]
                 [cprop "0.1.11"]
                 [clojure-lanterna "0.9.7"]
                 [clj-time "0.14.0"]]
  :main ^:skip-aot check-auth.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
