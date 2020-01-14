(ns clara-lein.ex05
  (:require [clara.rules :refer :all]
            [clojure.math.combinatorics :refer :all]))

;;;; A logic puzzle
;;;; 
;;;; Code up logic puzzle rules with clara.  Use rules to code the problem
;;;; constraints.  Use a 'for list comprehension to generate possible solutions
;;;; to choose among.
;;;;
;;;; See: https://programming-puzzler.blogspot.com/2013/03/logic-programming-is-overrated.html


;;;; Symbols that will be used
(def people
  '(amaya bailey jamari jason landon))

(def mag-syms
  '(fortune time cosmopolitan us-weekly vogue))

(def cheese-syms
  '(asiago blue-cheese mascarpone mozzarella muenster))

(def time-syms
  '(five six seven seven-thirty eight-thirty))

;; Try to create a form to evaluate
;; My solution, fwiw
;;(eval (list 'defrecord 'Candidate (vec (reduce concat (list (list 'id) mag-syms cheese-syms time-syms))))) 

;; slack solution in beginner's channel
;; https://clojurians.slack.com/archives/C053AK3F9/p1578796869371900
(eval `(~'defrecord ~'Candidate [~'id ~@mag-syms ~@cheese-syms ~@time-syms])) 
 
;;;; Make a seq of all candidates
(def cands
  (let [pkeyperm (-> (map keyword people) permutations)
        mkey (map keyword mag-syms) 
        ckey (map keyword cheese-syms)
        tkey (map keyword time-syms)
        cand-maps (for [mpval pkeyperm
                        cpval pkeyperm
                        tpval pkeyperm]
                    (zipmap (concat mkey ckey tkey) (concat mpval cpval tpval)))
        cand-short (take 3 cand-maps)]
    (map map->Candidate (map #(into %1 {:id %2}) cand-short (range (count cand-short))))))



