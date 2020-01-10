(ns clara-lein.ex01
  (:require [clara.rules :refer :all]))

;;; Taken directly from https://github.com/cerner/clara-rules
;;; But my slight modification: try to make a vector of facts to insert.


;;; Define records to use
(defrecord SupportRequest [client level])

(defrecord ClientRepresentative [name client])



;;; Define facts in this var, to be used below
(def facts [(->ClientRepresentative "Alice" "Acme")
            (->SupportRequest "Acme" :high)])




;;; Define rules
(defrule is-important
  "Find important support requests."
  [SupportRequest (= :high level)]
  =>
  (println "High support requested!"))

(defrule notify-client-rep
  "Find the client representative and send a notification of a support request."
  [SupportRequest (= ?client client)]
  [ClientRepresentative (= ?client client) (= ?name name)] ; Join via the ?client binding.
  =>
  (println "Notify" ?name "that"  ?client "has a new support request!"))



;;; Fire rules, the facts are stored in `facts` var.
(fire-rules (apply insert (flatten [(mk-session) facts])))



;;;; Prints this:

;; High support requested!
;; Notify Alice that Acme has a new support request!

