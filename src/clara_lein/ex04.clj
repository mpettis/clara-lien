(ns clara-lein.ex04
  (:require [clara.rules :refer :all]
            [clojure.math.combinatorics :refer :all]))

;;;; A logic puzzle
;;;; 
;;;; Code up logic puzzle rules with clara.  Use rules to code the problem
;;;; constraints.  Use a 'for list comprehension to generate possible solutions
;;;; to choose among.
;;;;
;;;; See: https://programming-puzzler.blogspot.com/2013/03/logic-programming-is-overrated.html

;; This generates permutations
(let [people [:amaya :bailey :jamari :jason :landon]]
      (for [[fortune time cosmopolitan us-weekly vogue] (permutations people)]
        (zipmap people [fortune time cosmopolitan us-weekly vogue])))

