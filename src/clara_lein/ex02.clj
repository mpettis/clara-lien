(ns clara-lein.ex02
  (:require [clara.rules :refer :all]))



;;; Define records to use
(defrecord Temperature [id value])




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
  (println (str "Temperature too low: id = " ?id ", value = " ?value)))

(defrule high-temp
  "Find Temperatures that are too high."
  [Temperature (= ?id id) (= ?value value)]
  [:test (> ?value 75)]
  =>
  (println (str "Temperature too high id = " ?id ", value = " ?value)))




;;; Fire rules, the facts are stored in `facts` var.
;; (fire-rules (apply insert (flatten [(mk-session) facts])))

(defn run-ex
  []
  (let [sess (mk-session)]
    (fire-rules (apply insert (flatten [sess facts])))))

