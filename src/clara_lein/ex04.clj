(ns clara-lein.ex04
  (:require [clara.rules :refer :all]
            [clojure.math.combinatorics :refer :all]))

;;;; A logic puzzle
;;;; 
;;;; https://www.brainzilla.com/logic/logic-grid/basic-1/
;;;;
;;;; Full list:
;;;; - 3 kids: Arnold, Eric, Peter
;;;; - 3 ages: 7yo, 8yo, 9yo
;;;; - 3 birthdays: January, April, September
;;;;
;;;; Clues:
;;;; - Peter's birthday is April.
;;;; - Eric is 7yo.
;;;; - Arnold's birthday is September.
;;;; - Peter is 8yo.
;;;;



;;; Makes all of the yo/birthday combos

;(let [people [:arnold :eric :peter]]
;  (for [[yo7 yo8 yo9] (permutations people)
;        [january april september] (permutations people)]
;    (merge
;      (zipmap (map keyword '[yo7 yo8 yo9]) [yo7 yo8 yo9])
;      (zipmap (map keyword '[january april september]) [january april september]))))



;;; Define facts
;;; These are potential combinations that may pass rule assertions.
(def facts-possible-combos 
  (let [people [:arnold :eric :peter]]
    (for [[yo7 yo8 yo9] (permutations people)
          [january april september] (permutations people)]
      (merge
        (zipmap (map keyword '[yo7 yo8 yo9]) [yo7 yo8 yo9])
        (zipmap (map keyword '[january april september]) [january april september])))))



