(ns music
  (:require [alda.core :refer :all]))

(comment
  (connect!)
  (new-score!)
  (stop!)

  (play!
    "piano: c+2 e8 g+4.")

  ;; alda-clj has data structures that map exactly to Alda syntax:
  (play!
    ;; piano: c+2 e8 g+4.
    (part "piano")
    ;; C# half note
    (note (pitch :c :sharp) (note-length 2))
    ;; E eighth note
    (note (pitch :e) (note-length 8))
    ;; G# dotted quarter note
    (note (pitch :g :sharp) (note-length 4 {:dots 1})))













  ;; ...but for our purposes, it's more convenient to use MIDI note numbers and
  ;; durations expressed in milliseconds:
  (play!
    (part "piano")
    (note (midi-note 42) (ms 100))
    (note (midi-note 43) (ms 200))
    (note (midi-note 44) (ms 400))
    (note (midi-note 45) (ms 800))
    (note (midi-note 46) (ms 1600))
    (note (midi-note 47) (ms 3200)))


  ;; The same notes, but produced programmatically:
  (play! ; [action]
    (part "piano")
    (map
      ;; [calculation] numbers -> note
      #(note (midi-note %1) (ms %2))
      ;; [data] The numbers 42-47
      (range 42 48)
      ;; [data] The first 6 numbers in the sequence 100, 200, 400...
      (take 6 (iterate #(* % 2) 100))))















  ;;; map, range

  (defn chromatic-scale
    [note-length-ms]
    (map #(note (midi-note %) (ms note-length-ms))
         (range 24 102)))

  (play!
    (part "harp")
    (chromatic-scale 50))













  ;;; filter

  (defn note-number
    [note]
    (get-in note [:pitch :note-number]))

  (note-number (note (midi-note 42)))

  (count (chromatic-scale 50))

  (count (filter #(even? (note-number %))
                 (chromatic-scale 50)))

  (play!
    (part "harp")
    (chromatic-scale 100))

  (play!
    (part "harp")
    (filter #(even? (note-number %))
            (chromatic-scale 100)))

  (play!
    (part "harp")
    (filter #(-> % note-number (mod 2) zero?)
            (chromatic-scale 100)))












  ;;; repeatedly, rand-nth, cycle, take

  (rand-nth [1 2 3 4 5])
  (repeatedly 3 #(rand-nth [1 2 3 4 5]))

  (let [note-numbers (repeatedly 3 #(rand-nth (range 24 102)))]
    (prn :note-numbers note-numbers)
    (play!
      (part "harp")
      (->> (cycle note-numbers)
           (take 48)
           (map #(note (midi-note %) (ms 150))))))
















  ;;; interpose

  (interpose 'x '[a b c d e f g])

  (def notes
    (let [high-note (note (midi-note 100) (ms 150))
          low-notes (map
                      #(note (midi-note %) (ms 150))
                      (range 20 40))]
      (interpose high-note low-notes)))

  (play!
    (part "piano")
    notes)












  ;;; mapcat

  (defn prime-factors
    [n]
    (loop [n         n
           candidate 2
           factors   []]
      (cond
        (< n 2)
        factors

        (zero? (mod n candidate))
        (recur (/ n candidate) candidate (conj factors candidate))

        :else
        (let [next-candidate (if (= 2 candidate)
                               3
                               (+ 2 candidate))]
          (recur n next-candidate factors)))))

  (map prime-factors (range 10))
  (mapcat prime-factors (range 10))















  (def notes
    (mapcat
      (fn [n] (let [factors (prime-factors n)]
                (map
                  #(note (midi-note (+ n %))
                         (ms (/ 500.0 (count factors))))
                  factors)))
      (range 40 60)))

  (play!
    (part "marimba")
    notes)



















  (defn periodic-notes
    [divisor note-number & [{:keys [include-zero?]}]]
    (map
      (fn [n]
        (if (or
              (and include-zero? (zero? n))
              (and (pos? n) (zero? (rem n divisor))))
          (note (midi-note note-number) (ms 135))
          (pause (ms 135))))
      (range 96)))

  (defn periodic-notes-inverse
    [exclude-divisors note-number]
    (map
      (fn [n] (if (every? #(pos? (rem n %)) exclude-divisors)
                (note (midi-note 42) (ms 135))
                (pause (ms 135))))
      (range 96)))

  (do
    (connect!)
    (new-score!)
    (play!
      ;; fizz (divisible by 3)
      (part "oboe") (vol 62) (pan 25)
      (periodic-notes 3 80)

      ;; buzz (divisible by 5)
      (part "clarinet") (vol 57) (pan 75)
      (periodic-notes 5 68)

      (part "midi-percussion")
      ;; hi-hat on every number that isn't divisible by 3 or 5
      (voice 1) (vol 45)
      (periodic-notes-inverse #{3 5} 42)
      ;; kick drum
      (voice 2) (periodic-notes 5 35 {:include-zero? true})
      ;; triangle
      (voice 3) (periodic-notes 15 81 {:include-zero? true}))))
