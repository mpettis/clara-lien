(ns clara-lein.ex08
  "More complex logic problem:

  (~F v P) => (~M ^ D)

  Fill in some values for A, B, C, and D, and determine truth value of proposition.

  Example of the statement as English propositions.
  If the fridge is not closed (~F) or there are pants in the entryway (P),
  then Mom is not home (~M) and Dad is home (D).

  We are looking for truth values of the final implication.

  https://dsearls.org/courses/M120Concepts/ClassNotes/Logic/130A_examples.htm
  "
  (:require 
    [clara.rules :refer :all]))



;;; Records for initial facts
(defrecord F [truth-value])
(defrecord P [truth-value])
(defrecord M [truth-value])
(defrecord D [truth-value])


;;; Records for derived facts
;;; Let's call ~F v P the House-observation, and
;;; ~M ^ D the Parent-observation

(defrecord House-observation [truth-value])
(defrecord Parent-observation [truth-value])


;;; Full implication
(defrecord House-parent-implication [truth-value])


;;; Rule
(defrule rule-house-obs
  "What is the combined truth value of the House observations."
  [F (= ?f-truth truth-value)]
  [P (= ?p-truth truth-value)]
  ;[:test (or (not= ?f-truth :true) (= ?p-truth :true))]
  =>
  ;[(insert! (->House-observation :true))]
  [(insert! (->House-observation (if (or (not= ?f-truth :true) (= ?p-truth :true)) :true :false)))]
  )

(defrule rule-parent-obs
  "What is the combined truth value of the Parent."
  [M (= ?m-truth truth-value)]
  [D (= ?d-truth truth-value)]
  ;[:test (and (not= ?m-truth :true) (= ?d-truth :true))]
  =>
  ;[(insert! (->Parent-observation :true))]
  [(insert! (->Parent-observation (if (and (not= ?m-truth :true) (= ?d-truth :true)) :true :false)))]
  )

(defrule rule-house-parent-material-implication
  "What is the combined truth value of the Home-parent implication."
  [House-observation (= ?house-truth truth-value)]
  [Parent-observation (= ?parent-truth truth-value)]
  ;[:test (or (not= ?house-truth :true) (= ?parent-truth :true))]
  =>
  ;[(insert! (->House-parent-implication :true))]
  [(insert! (->House-parent-implication (if (or (not= ?house-truth :true) (= ?parent-truth :true)) :true :false)))]
  )



;;; Query
(defquery query-house-parent-implication
  "Final implication truth"
  []
  [?q <- House-parent-implication])

(defquery query-house-obs
  "House implication aggregate."
  []
  [?q <- House-observation])

(defquery query-parent-obs
  "Parent implication aggregate."
  []
  [?q <- Parent-observation])



;;; Session execution
(comment
  (for [f [:true :false]
        p [:true :false]
        m [:true :false]
        d [:true :false]]
    (let [sess (->
                 (mk-session)
                 (insert (->F f) (->P p) (->M m) (->D d))
                 fire-rules)
          qhpi (query sess query-house-parent-implication)
          qh (query sess query-house-obs)
          qp (query sess query-parent-obs)
          ]
      [f p m d qhpi qh qp])))

