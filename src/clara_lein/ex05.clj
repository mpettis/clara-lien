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
(def people
  [:amaya :bailey :jamari :jason :landon])

(def people-perms
  (permutations people))


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
`(~'defrecord ~'Candidate [~'id ~@mag-syms ~@cheese-syms ~@time-syms]) 
 

;; Example: this builds a simple map list of what I want
;(eval
;  `(~'for [~(vec mag-syms) ~'people-perms]
;     ~(zipmap (map keyword mag-syms) (vec mag-syms))))


(eval)
  `(~'for [~(vec mag-syms) ~'people-perms
           ~(vec cheese-syms) ~'people-perms]
    ()
     )


;; Assembles vector
;(-> 
;  (let [cand-maps (eval `(~'for [~(vec mag-syms) ~'people-perms
;                                 ~(vec cheese-syms) ~'people-perms
;                                 ~(vec time-syms) ~'people-perms]
;                           [~@mag-syms ~@cheese-syms ~@time-syms]))
;        ids (range (count cand-maps))
;        ]
;    (map #(into %2 {:id %1}) ids cand-maps)
;    )
;  first
;  )








;(-> 
;  (let [cand-maps (eval `(~'for [~(vec mag-syms) ~'people-perms
;                                 ~(vec cheese-syms) ~'people-perms
;                                 ~(vec time-syms) ~'people-perms]
;                           [~@mag-syms ~@cheese-syms ~@time-syms]
;                           ))
;        ids (range (count cand-maps))
;        ]
;    (map #(into %2 {:id %1}) ids cand-maps)
;    )
;  first
;  )


;(for [[fortune time cosmopolitan us-weekly vogue] people-perms
;      [asiago blue-cheese mascarpone mozzarella muenster] people-perms
;      [five six seven seven-thirty eight-thirty] people-perms]
  
;  )



;(let [people [:amaya :bailey :jamari :jason :landon]
;      items  (for [[fortune time cosmopolitan us-weekly vogue] (permutations people) ; magazines
;                   [asiago blue-cheese mascarpone mozzarella muenster] (permutations people) ; cheeses
;                   ; We bind the reservations in two steps, so we have a name for the overall order
;                   reservations (permutations people)]
;               1)]
;  (count items))


;;; All candidate combinations
;(def candidates 
;  (let [people [:amaya :bailey :jamari :jason :landon]]
;    (for [[fortune time cosmopolitan us-weekly vogue] (permutations people) ; magazines
;          [asiago blue-cheese mascarpone mozzarella muenster] (permutations people) ; cheeses
;          ; We bind the reservations in two steps, so we have a name for the overall order
;          reservations (permutations people)
;          :let [[five six seven seven-thirty eight-thirty] reservations
;                candidate (->Candidate fortune time cosmopolitan us-weekly vogue
;                                       asiago blue-cheese mascarpone mozzarella muenster
;                                       five six seven seven-thirty eight-thirty)]]
;      candidate)))


;;; All candidate combinations
;(def candidates 
;  (let [people [:amaya :bailey :jamari :jason :landon]
;        candidate-map (for [[fortune time cosmopolitan us-weekly vogue] (permutations people) ; magazines
;                            [asiago blue-cheese mascarpone mozzarella muenster] (permutations people) ; cheeses
;                            ; We bind the reservations in two steps, so we have a name for the overall order
;                            reservations (permutations people)
;                            :let [[five six seven seven-thirty eight-thirty] reservations]]
;                        zipmap (map keyword [fortune time cosmopolitan us-weekly vogue asiago blue-cheese mascarpone mozzarella muenster five six seven seven-thirty eight-thirty])
;                               [fortune time cosmopolitan us-weekly vogue asiago blue-cheese mascarpone mozzarella muenster five six seven seven-thirty eight-thirty])
;        ]))

