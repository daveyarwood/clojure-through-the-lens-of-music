(ns shapes)

(def ■ '■)
(def ▲ '▲)
(def ● '●)

(comment
  [■ ▲ ●]

  ;; repeat
  (repeat ■)
  (repeat 10 ■)

  ;; take
  (take 3 (repeat ■))

  ;; cycle
  (cycle [■ ▲ ●])
  (take 10 (cycle [■ ▲ ●]))

  ;; drop
  (drop 2 [■ ▲ ●])
  (take 10 (drop 2 (cycle [■ ▲ ●])))

  ;; equivalent to the previous example
  (->> (cycle [■ ▲ ●])
       (drop 2)
       (take 10))

  ;; filter
  (->> (cycle [■ ▲ ●])
       (filter #{■ ●})
       (take 10))

  ;; remove
  (->> (cycle [■ ▲ ●])
       (remove #{■ ●})
       (take 10)))
