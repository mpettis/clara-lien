(ns clara-lein.scratch
  (:require [clara.rules :refer :all]
            [clojure.math.combinatorics :refer :all]))

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
(defrecord Rulefail [rule-name candidate-id])

(defrule rule-1
  "Peter's birthday is April."
  [Candidate (= ?id id)]
  [:not [Candidate (= ?id id) (= april :peter)]]
  =>
  (insert! (->Rulefail "rule-1" ?id))
)



;;; Queries
(defquery get-rulefail
  "Get failed rules"
  []
  [?rulefail <- Rulefail])



;;; Session and fire
(let [sess-init (mk-session)
      sess-facts (apply insert (flatten [sess-init candidates]))
      sess-fired (fire-rules sess-facts)]
  (query sess-fired get-rulefail))


