(ns clara-lein.ex03
  (:require [clara.rules :refer :all]))

;;;; Let's do a high/low temperature example, but instead of just
;;;; printing a warning, let's insert records to indicate issues.  And then
;;;; query them.


;;; Example commands for repl... not necessary.
;; (in-ns 'clara-lein.ex03)
;; (ns 'clara-lein.ex03)
;; (use 'clara-lein.ex03 :reload)



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






;;; Queries
(defquery get-tempstates
  "Query to get Tempstates"
  []
  [?tempstate <- Tempstate])



;;;; Execute

;;; Fire rules, the facts are stored in `facts` var.
;;(fire-rules (apply insert (flatten [(mk-session) facts])))

;;; Typical query template -- doesn't work as coded, need to assign 'session
;; (query session get-tempstates)



;;; Fire rules and get results
(let [sess-init (mk-session)
      sess-facts (apply insert (flatten [sess-init facts]))
      sess-fired (fire-rules sess-facts)]
  (query sess-fired get-tempstates))

