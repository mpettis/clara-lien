(ns clara-lein.ex04
  (:require [clara.rules :refer :all]
            [clojure.math.combinatorics :refer :all]
            [clojure.set]))

;;;; A logic puzzle
;;;; 
;;;; https://www.brainzilla.com/logic/logic-grid/basic-1/
;;;;
;;;; Full list:
;;;; - 3 kids: Arnold, Eric, Peter
;;;; - 3 ages: 7yo, 8yo, 9yo
;;;; - 3 birthdays: January, April, September
;;;;
;;;; Clues:
;;;; - Peter's birthday is April.
;;;; - Eric is 7yo.
;;;; - Arnold's birthday is September.
;;;; - Peter is 8yo.
;;;;
;;;; Logic of generators based on logic found here:
;;;; https://programming-puzzler.blogspot.com/2013/03/logic-programming-is-overrated.html
;;;;

;;; Candidate facts
(defrecord Candidate [id yo7 yo8 yo9 january april september])

;; Makes seq of Candidate records
(def candidates
  (let [people [:arnold :eric :peter]
        map-list (for [[yo7 yo8 yo9] (permutations people)
                       [january april september] (permutations people)]
                   (merge
                     (zipmap (map keyword '[yo7 yo8 yo9]) [yo7 yo8 yo9])
                     (zipmap (map keyword '[january april september]) [january april september])))
        id-maps (for [i (range (count map-list))] {:id i})
        full-maps (map #(merge %1 %2) id-maps map-list)
        rec-list (map map->Candidate full-maps)]
    rec-list))



;;; Rules
;;;
;;; Rule will insert a record if the rule condition fails on a candidate
(defrecord Rulefail [rule-name candidate-id])

(defrule rule-1
  "Peter's birthday is April."
  [Candidate (= ?id id)]
  [:not [Candidate (= ?id id) (= april :peter)]]
  =>
  (insert! (->Rulefail "rule-1" ?id)))

(defrule rule-2
  "Eric is 7yo."
  [Candidate (= ?id id)]
  [:not [Candidate (= ?id id) (= yo7 :eric)]]
  =>
  (insert! (->Rulefail "rule-2" ?id)))

(defrule rule-3
  "Arnold's birthday is September."
  [Candidate (= ?id id)]
  [:not [Candidate (= ?id id) (= september :arnold)]]
  =>
  (insert! (->Rulefail "rule-3" ?id)))

(defrule rule-4
  "Peter is 8yo."
  [Candidate (= ?id id)]
  [:not [Candidate (= ?id id) (= yo8 :peter)]]
  =>
  (insert! (->Rulefail "rule-4" ?id)))




;;; Queries
(defquery get-rulefail
  "Get failed rules"
  []
  [?rulefail <- Rulefail])



;;; Session and fire
(let [sess-init (mk-session)
      sess-facts (apply insert (flatten [sess-init candidates]))
      sess-fired (fire-rules sess-facts)
      ;; All Rulefail objects returned in query
      results (query sess-fired get-rulefail)
      ;; Pull out ids of all candidates that failed a test, regardless of which test.
      fail-candidate-ids (keys (group-by :candidate-id (map (comp first vals) results)))
      ;; Collect ids for all failed candidates, setdiff possible candidate ids with failed ones.  Should leave 1 left, the correct answer.
      id-success (first (clojure.set/difference (set (map :id candidates))  (set fail-candidate-ids)))]
  ;; Retrieve record that has a successful id.
  (println "Successful assignment")
  (filter #(= id-success (:id %)) candidates))

;;;; Answer gotten:
;;;; (#clara_lein.ex04.Candidate{:id 21, :yo7 :eric, :yo8 :peter, :yo9 :arnold, :january :eric, :april :peter, :september :arnold})
