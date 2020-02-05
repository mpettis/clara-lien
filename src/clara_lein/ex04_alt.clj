(ns clara-lein.ex04-alt
  (:require [clara.rules :as c :refer [defrule defquery insert!]]))

(defrecord Value [attr value])
(defrecord Guess [name attr value])
(defrecord Person [name age birthday])

(def facts [(->Value :age 7)
            (->Value :age 8)
            (->Value :age 9)
            (->Value :birthday :january)
            (->Value :birthday :april)
            (->Value :birthday :september)])

(defrule generate-combinations
  ;[?f <- Value (= attr ?a) (= value ?v)]
  [Value (= attr ?a) (= value ?v)]
  =>
  (insert! (->Guess "Arnold" ?a ?v)
           (->Guess "Eric" ?a ?v)
           (->Guess "Peter" ?a ?v)))

(defrule find-solution
  [Guess (= name "Arnold") (= attr :age) (= value ?a-age)]
  [Guess (= name "Eric")   (= attr :age) (= value ?e-age)]
  [Guess (= name "Peter")  (= attr :age) (= value ?p-age)]
  [Guess (= name "Arnold") (= attr :birthday) (= value ?a-birthday)]
  [Guess (= name "Eric")   (= attr :birthday) (= value ?e-birthday)]
  [Guess (= name "Peter")  (= attr :birthday) (= value ?p-birthday)]

  ;; Peter's birthday is in April.
  [:test (= ?p-birthday :april)]
  ;; Eric is 7.
  [:test (= ?e-age 7)]
  ;; Arnold's birthday is in September.
  [:test (= ?a-birthday :september)]
  ;; Peter is 8.
  [:test (= ?p-age 8)]
  ;; Everyone has different ages
  [:test (= 3 (count (hash-set ?a-age ?e-age ?p-age)))]
  ;; Everyone has different birthdays
  [:test (= 3 (count (hash-set ?a-birthday ?e-birthday ?p-birthday)))]
  =>
  (insert! (->Person "Arnold" ?a-age ?a-birthday)
           (->Person "Eric"   ?e-age ?e-birthday)
           (->Person "Peter"  ?p-age ?p-birthday)))

(defquery test-query
  []
  [?f <- Person])

(defn run
  []
  (-> (c/mk-session)
      (c/insert-all facts)
      (c/fire-rules)
      (c/query test-query)))

(comment
  (run)

  ;; result
  ;; ({:?f {:name "Arnold", :age 9, :birthday :september}}
  ;;  {:?f {:name "Eric", :age 7, :birthday :january}}
  ;;  {:?f {:name "Peter", :age 8, :birthday :april}})
  )
