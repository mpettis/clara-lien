(ns clara-lein.ex01
  "Illustrate the following:
    1. Simple Modus Ponens.

         Modus Ponens:
           P => Q
           P
           ------
           Q
      We do this by making a rule for P => Q, and inserting fact P.
      When we fire the rules, Q gets inserted as a fact, and we can query for it.

  2. Facts that are not present can trigger rules with `:not` operator.
    We can fire a rule for the non-presence of a fact, and insert a fact based on that.
  "
  (:require [clara.rules :refer :all]))





;;; Session 1: Simple Modus Ponens
(ns sess1
  (:require [clara.rules :refer :all]))

;;; Records
(defrecord P [])
(defrecord Q [])

;;; If P is true (present), and we have the rule P => Q,
;;; then Q becomes a generated fact.
(defrule rule-modus-ponens
  "Pure Modus Ponens rule.  P => Q, which here means that if P is a true fact, insert true fact Q."
  [P]
  =>
  (insert! (->Q)))


;;;; Query for presence of Q
(defquery has-q
  []
  [?q <- Q])


;;;; Run the session
(let [sess (->
             (mk-session)
             (insert (->P))
             fire-rules)]
  (query sess has-q))





;;; Session 2: Rule fires when fact not present
(ns sess2
  (:require [clara.rules :refer :all]))

;;; Records
(defrecord P [])
(defrecord Q [])

;;; If P is true (present), and we have the rule P => Q,
;;; then Q becomes a generated fact.
(defrule rule-not-modus-ponens
  "Not Modus Ponens rule.  ~P => Q, which here means that if P is false, or does not exist, insert true fact Q."
  [:not [P]]
  =>
  (insert! (->Q)))


;;;; Query for presence of Q
(defquery has-q
  []
  [?q <- Q])


;;;; Run the session
;;;; Notice no fact P was inserted.
(let [sess (->
             (mk-session)
             fire-rules)]
  (query sess has-q))


