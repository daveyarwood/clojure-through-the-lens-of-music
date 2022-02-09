(ns stdlib-exploration
  (:require [clojure.string :as str]))

(def ^:const clojure-core-functions
  (vals (ns-publics 'clojure.core)))

(defn random-clojure-core-function
  []
  (rand-nth clojure-core-functions))

(defn function-info-string
  [function]
  (let [{:keys [name arglists doc added]} (meta function)]
    (format "%s%s\n%s\n%s"
            name
            (when added
              (format " (added %s)" added))
            arglists
            doc)))

(defn print-random-clojure-core-functions!
  "A CLI entrypoint to print information about random clojure.core functions."
  [{:keys [n]}]
  (let [function-infos
        (repeatedly
          (or n 1)
          #(function-info-string (random-clojure-core-function)))]
    (println (str/join "\n\n" function-infos))))
