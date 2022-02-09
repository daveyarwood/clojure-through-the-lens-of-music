(ns stdlib-exploration
  (:require [clojure.string :as str]))

(def ^:const all-clojure-core-functions
  (->> (ns-publics 'clojure.core)
       vals
       (filter #(fn? @%))))

(defn- strings-in-composite
  [x]
  (cond
    (string? x)
    [x]

    (or (symbol? x) (keyword? x))
    [(name x)]

    (map? x)
    (concat (map strings-in-composite (keys x))
            (map strings-in-composite (vals x)))

    (coll? x)
    (apply concat (map strings-in-composite x))))

(defn clojure-core-functions
  [& {:keys [search]}]
  (->> all-clojure-core-functions
       (filter (fn [function]
                 (let [{:keys [arglists doc], fn-name :name} (meta function)]
                   (or (not search)
                       (str/includes? (name fn-name) search)
                       (and doc (str/includes? doc search))
                       (some
                         #(str/includes? % search)
                         (strings-in-composite arglists))))))))

(defn random-clojure-core-functions
  [& [{:keys [n] :as opts}]]
  (->> (clojure-core-functions opts)
       shuffle
       (take (or n 1))))

(defn- function-info-string
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
  [opts]
  (let [function-infos
        (map function-info-string (random-clojure-core-functions opts))]
    (println (str/join "\n\n" function-infos))))
