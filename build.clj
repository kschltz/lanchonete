(ns build
  (:refer-clojure :exclude [test])
  (:require [clojure.java.shell :as sh]
            [clojure.tools.deps :as t]
            [clojure.tools.build.api :as b]))

(def lib 'net.clojars.mba-fiap/lanchonete)
(def version "0.1.0-SNAPSHOT")
(def main 'mba-fiap.lanchonete)
(def class-dir "target/classes")

(defn test "Run all the tests." [opts]
  (println "\nRunning tests...")
  (let [basis (b/create-basis {:aliases [:test]})
        combined (t/combine-aliases basis [:test])
        cmds (b/java-command
               {:basis     basis
                :java-opts (:jvm-opts combined)
                :main      'clojure.main
                :main-args ["-m" "cognitect.test-runner"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Tests failed" {}))))
  opts)

(defn- uber-opts [opts]
  (assoc opts
    :lib lib :main main
    :uber-file (format "target/%s-%s.jar" lib version)
    :basis (b/create-basis {})
    :class-dir class-dir
    :src-dirs ["src"]
    :ns-compile [main]))

(defn ci "Run the CI pipeline of tests (and build the uberjar)." [opts]

  (b/delete {:path "target"})
  (let [opts (uber-opts opts)]
    (println "\nCopying source...")
    (b/copy-dir {:src-dirs ["resources" "src"] :target-dir class-dir})
    (println (str "\nCompiling " main "..."))
    (b/compile-clj opts)
    (println "\nBuilding JAR...")
    (b/uber opts)
    (test opts)
    (when (:bdd opts)
      (println "\nRunning Cucumber tests...\n"
               (sh/sh "clojure" "-M:test:cucumber" "-g" "./test/mba_fiap/" "./test/resources/"))))
  opts)

(comment
  (b/compile-clj (uber-opts {})))
