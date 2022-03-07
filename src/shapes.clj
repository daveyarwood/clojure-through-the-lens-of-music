(ns shapes)

(def ■ '■)
(def ▲ '▲)
(def ● '●)

(comment
  ;; cycle, take, drop
  (take 10 (cycle [■ ▲ ●]))

  (take 10 (drop 1 (cycle [■ ▲ ●])))

  ;; equivalent to the previous example
  (->> (cycle [■ ▲ ●])
       (drop 1)
       (take 10))

  ;; filter
  (->> (cycle [■ ▲ ●])
       (filter #{■ ●})
       (take 10))

  ;; remove
  (->> (cycle [■ ▲ ●])
       (remove #{■ ●})
       (take 10)))
