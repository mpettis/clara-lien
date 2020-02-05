(ns clara-lein.ex07
  "First order boolean logic.
  Modus Ponens:
    P => Q
    P
    ------
    Q

  Here, 'P => Q' is the rule, 'P' is the initial fact, 'Q' is the derived fact we query for."
  (:require 
    [clara.rules :refer :all]))





;;; Fact records
(defrecord P [truth-value])
(defrecord Q [truth-value])


;;; Rule
(defrule rule-modus-ponens
  "Pure Modus Ponens rule.  P => Q, which here means that if P is a true fact, insert true fact Q."
  [P (= ?p-truth truth-value)]
  [:test (= ?p-truth :true)]
  =>
  [(insert! (->Q :true))])

;;; Query
(defquery has-q
  []
  [?q <- Q])


;;; Run session
(comment
  (let [sess (->
               (mk-session)
               (insert (->P :true))
               fire-rules)]
    (query sess has-q)))


