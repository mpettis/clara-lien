(ns clara-lein.scratch
  (:require [clara.rules :refer :all]
            [clojure.math.combinatorics :refer :all]
            [clojure.set]))


(defrecord P [])
(defrecord Q [])


;;;
;;; If P is true (present), and we have the rule P => Q,
;;; then Q becomes a generated fact.
;;;

(comment
  (defrule rule-modus-ponens
    "Pure Modus Ponens rule.  P => Q, which here means that if P is a true fact, insert true fact Q."
    [P]
    =>
    (insert! (->Q)))

  (defquery has-q
    []
    [?q <- Q])


  (let [sess (->
               (mk-session)
               (insert (->P))
               fire-rules)]
    (query sess has-q)))





;;; Check that we can fire rules if facts are not present

(defrule rule-not-modus-ponens
  "Pure Modus Ponens rule.  P => Q, which here means that if P is a true fact, insert true fact Q."
  [:not [P]]
  =>
  (insert! (->Q)))

(defquery has-q
  []
  [?q <- Q])


;; Session has no P fact inserted
(let [sess (->
             (mk-session)
             fire-rules)]
  (query sess has-q))




