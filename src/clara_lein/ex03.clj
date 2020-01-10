(ns clara-lein.ex03
  (:require [clara.rules :refer :all]))

;; (use 'clara-lein.ex03 :reload)

;;;; Let's do the same high/low temperature example, but instead of just
;;;; printing a warning, let's insert records to indicate issues.  And then
;;;; query them.


;;; Define records to use
(defrecord Temperature [id value])
(defrecord Tempstate [id passfail reason])




;;; Define facts in this var, to be used below
(def facts [(->Temperature 1 65)
            (->Temperature 2 68)
            (->Temperature 3 71)
            (->Temperature 4 74)
            (->Temperature 5 77)])




;;; Define rules
(defrule low-temp
  "Find Temperatures that are too low."
  [Temperature (= ?id id) (= ?value value)]
  [:test (< ?value 70)]
  =>
  (insert! (->Tempstate ?id :fail "Low temp threshold")))


(defrule high-temp
  "Find Temperatures that are too high."
  [Temperature (= ?id id) (= ?value value)]
  [:test (> ?value 75)]
  =>
  (insert! (->Tempstate ?id :fail "High temp threshold")))




;;; Fire rules, the facts are stored in `facts` var.
(fire-rules (apply insert (flatten [(mk-session) facts])))


;;; Queries
(defquery get-tempstates
  "Query to get Tempstates"
  []
  [?tempstate <- Tempstate])



;;; Execute
;; (query session get-tempstates)

(defn run-ex
  []
  (let [sess (mk-session)
        sess-fired (fire-rules (apply insert (flatten [sess facts])))]
    (query sess-fired get-tempstates)))

(run-ex)

