(ns music
  (:require [alda.core :refer [connect! new-score! play! stop! part note pitch
                               note-length midi-note ms]]))

;; Rough timings:
;; Slides: 11 minutes
;; Shapes: 6 minutes
;; Music: 23 minutes
;; Text to music: 5 minutes
;;
;; = exactly 45 minutes!

;; TODO:
;; * Removes examples below that are less interesting
;; * Run through and time again
;; * Goal is to end up with time at the end for wiggle room / questions

(comment
  (connect!)
  (new-score!)
  (stop!)

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
  (play!
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












  ;;; reduce, reductions

  ;; This is an example of a major scale:
  (play!
    "midi-electric-piano-1:
       o4
       c d e f g a b > c")

  (def major-scale-intervals
    [2 2 1 2 2 2 1])

  ;; (-> 2 (+ 2) (+ 1) (+ 2) ...)
  (reduce + major-scale-intervals)

  ;; (-> 20 (+ 2) (+ 2) (+ 1) (+ 2) ...)
  (reduce + 20 major-scale-intervals)

  (reductions + major-scale-intervals)
  (reductions + 20 major-scale-intervals)

  (play!
    (part "trumpet")
    (map
      #(note (midi-note %) (ms 200))
      (reductions + 58 major-scale-intervals)))











  (defn scale
    [starting-note intervals]
    (map
      #(note (midi-note %) (ms 200))
      (reductions + starting-note intervals)))

  (def minor-scale-intervals
    [2 1 2 2 1 2 2])

  (def octatonic-scale-intervals
    [2 1 2 1 2 1 2 1])

  (play!
    (part "trumpet")
    (scale 58 minor-scale-intervals))

  (play!
    (part "trumpet")
    (scale 58 octatonic-scale-intervals))

















  (defn octatonic-scale
    [starting-note scale-length]
    (let [intervals (take scale-length (cycle [2 1]))]
      (scale starting-note intervals)))

  (play!
    (part "trumpet")
    (octatonic-scale 58 16))














  (defn random-scale
    [starting-note]
    (let [intervals (repeatedly 7 #(rand-nth [1 2 3]))]
      (prn :intervals intervals)
      (scale starting-note intervals)))

  (play!
    (part "trumpet")
    (random-scale 58))


  ;;; concat

  (play!
    (part "trumpet")
    (concat
      (random-scale 58)
      (random-scale 58)
      (random-scale 58)
      (random-scale 58))))
