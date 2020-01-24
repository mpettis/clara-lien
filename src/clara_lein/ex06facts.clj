(ns clara-lein.ex06facts)

;;; Fact defrecord
(defrecord Myfact [theval])

;;; Fact records
(def facts 
  [(->Myfact 0)
   (->Myfact 1)
   (->Myfact 2)
   (->Myfact 3)
   (->Myfact 4)])

