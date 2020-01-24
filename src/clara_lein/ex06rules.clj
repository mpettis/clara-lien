(ns clara-lein.ex06rules
  "Define session rules, to be imported to core."
  (:require 
    [clara-lein.ex06facts]
    [clara.rules :refer :all])
  (:import 
    (clara_lein.ex06facts Myfact)))



(defrecord Rulefail [theval])

(defrule rule-fail
  "Rule that checks for failure."
  [Myfact (= ?theval theval)]
  [:test (> ?theval 2)]
  =>
  (insert! (->Rulefail ?theval)))


