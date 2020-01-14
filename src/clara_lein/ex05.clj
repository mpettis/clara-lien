(ns clara-lein.ex05
  (:require [clara.rules :refer :all]
            [clojure.math.combinatorics :refer :all]))

;;;; A logic puzzle
;;;; 
;;;; Code up logic puzzle rules with clara.  Use rules to code the problem
;;;; constraints.  Use a 'for list comprehension to generate possible solutions
;;;; to choose among.
;;;;
;;;; See: https://programming-puzzler.blogspot.com/2013/03/logic-programming-is-overrated.html


;;;; Symbols that will be used
(def people
  '(amaya bailey jamari jason landon))

(def mag-syms
  '(fortune time cosmopolitan us-weekly vogue))

(def cheese-syms
  '(asiago blue-cheese mascarpone mozzarella muenster))

(def time-syms
  '(five six seven seven-thirty eight-thirty))

;; Put the time keywords in order
(def time-keywords
  (map keyword time-syms))


;; Try to create a form to evaluate
;; My solution, fwiw
;;(eval (list 'defrecord 'Candidate (vec (reduce concat (list (list 'id) mag-syms cheese-syms time-syms))))) 

;; slack solution in beginner's channel
;; https://clojurians.slack.com/archives/C053AK3F9/p1578796869371900
(eval `(~'defrecord ~'Candidate [~'id ~@mag-syms ~@cheese-syms ~@time-syms])) 
 


;;;; Make a seq of all candidates
(def cands
  (let [pkeyperm (-> (map keyword people) permutations)
        mkey (map keyword mag-syms) 
        ckey (map keyword cheese-syms)
        tkey (map keyword time-syms)
        cand-maps (for [mpval pkeyperm
                        cpval pkeyperm
                        tpval pkeyperm]
                    (zipmap (concat mkey ckey tkey) (concat mpval cpval tpval)))
        cand-short (take 999 cand-maps)]
    (map map->Candidate (map #(into %1 {:id %2}) cand-short (range (count cand-short))))))

;;; This is full one
;(def cands
;  (let [pkeyperm (-> (map keyword people) permutations)
;        mkey (map keyword mag-syms) 
;        ckey (map keyword cheese-syms)
;        tkey (map keyword time-syms)
;        cand-maps (for [mpval pkeyperm
;                        cpval pkeyperm
;                        tpval pkeyperm]
;                    (zipmap (concat mkey ckey tkey) (concat mpval cpval tpval)))]
;    (map map->Candidate (map #(into %1 {:id %2}) cand-maps (range (count cand-maps))))))






;;; Rule will insert a record if the rule condition fails on a candidate
(defrecord Rulefail [rule-name id])


;;;; Rules
;(defrule rule-1
;  "Of Landon and Jason, one has the 7:30pm reservation and the other loves mozzarella."
;  [Candidate (= ?id id)]
;  [:not [:or
;          [Candidate (= ?id id) (= seven-thirty :landon) (= mozzarella :jason)]
;          [Candidate (= ?id id) (= seven-thirty :jason) (= mozzarella :landon)]]]
;  =>
;  (insert! (->Rulefail "rule-1" ?id)))


;(defrule rule-2
;  "The blue cheese enthusiast subscribed to Fortune"
;  [Candidate (= ?id id) (= ?fortune fortune) (= ?blue-cheese blue-cheese)]
;  [:test (not= ?fortune ?blue-cheese)]
;  =>
;  (insert! (->Rulefail "rule-2" ?id)))


;(defrule rule-3
;  "The muenster enthusiast didn't subscribe to Vogue"
;  [Candidate (= ?id id) (= ?vogue vogue) (= ?muenster muenster)]
;  [:test (= ?vogue ?muenster)]
;  =>
;  (insert! (->Rulefail "rule-3" ?id)))


;(defrule rule-4
;  "The 5 people were the Fortune subscriber, Landon, the person with a reservation at 5:00pm, the mascarpone enthusiast, and the Vogue subscriber."
;  [Candidate (= ?id id) (= ?fortune fortune) (= ?five five) (= ?marscapone mascarpone) (= ?vogue vogue)]
;  [:test (not= 5 (count (set [?fortune :landon ?five ?marscapone ?vogue])))]
;  =>
;  (insert! (->Rulefail "rule-4" ?id)))


;(defrule rule-5
;  "The person with a reservation at 5:00pm didn't subscribe to Time."
;  [Candidate (= ?id id) (= ?five five) (= ?time time)]
;  [:test (not= ?time ?five)]
;  =>
;  (insert! (->Rulefail "rule-5" ?id)))


;(defrule rule-6
;  "The Cosmopolitan subscriber has an earlier reservation than the mascarpone enthusiast."
;  [Candidate (= ?id id) (= ?cosmopolitan cosmopolitan) (= ?mascarpone mascarpone) (= ?five five) (= ?six six) (= ?seven seven) (= ?seven-thirty seven-thirty) (= ?eight-thirty eight-thirty)]
;  [:test (let [reservations [?five ?six ?seven ?seven-thirty ?eight-thirty]
;               cosmopolitan-index (.indexOf reservations ?cosmopolitan)
;               mascarpone-index (.indexOf reservations ?mascarpone)]
;           (not (< cosmopolitan-index mascarpone-index)))]
;  =>
;  (insert! (->Rulefail "rule-6" ?id)))

;(defrule rule-7
;  "Bailey has a later reservation than the blue cheese enthusiast."
;  [Candidate (= ?id id) (= ?blue-cheese blue-cheese) (= ?five five) (= ?six six) (= ?seven seven) (= ?seven-thirty seven-thirty) (= ?eight-thirty eight-thirty)]
;  [:test (let [reservations [?five ?six ?seven ?seven-thirty ?eight-thirty]
;               bailey-index (.indexOf reservations :bailey)
;               blue-cheese-index (.indexOf reservations ?blue-cheese)]
;           (not (< blue-cheese-index bailey-index)))]
;  =>
;  (insert! (->Rulefail "rule-7" ?id)))

;(defrule rule-8
;  "Either the person with a reservation at 7:00pm or the person with a reservation at 7:30pm subscribed to Fortune."
;  [Candidate (= ?id id) (= ?fortune fortune) (= ?seven seven) (= ?seven-thirty seven-thirty)]
;  [:test (not (or
;           (= ?fortune ?seven)
;           (= ?fortune ?seven-thirty)))]
;  =>
;  (insert! (->Rulefail "rule-8" ?id)))

;(defrule rule-9
;  "Landon has a later reservation than the Time subscriber."
;  [Candidate (= ?id id) (= ?time time) (= ?five five) (= ?six six) (= ?seven seven) (= ?seven-thirty seven-thirty) (= ?eight-thirty eight-thirty)]
;  [:test (let [reservations [?five ?six ?seven ?seven-thirty ?eight-thirty]
;               landon-index (.indexOf reservations :landon)
;               time-index (.indexOf reservations ?time)]
;           (not (< time-index landon-index)))]
;  =>
;  (insert! (->Rulefail "rule-9" ?id)))

;(defrule rule-10
;  "The Fortune subscriber is not Jamari."
;  [Candidate (= ?id id) (= ?fortune fortune)]
;  [:test (not (= ?fortune :jamari))]
;  =>
;  (insert! (->Rulefail "rule-10" ?id)))

(defrule rule-11
  "The person with a reservation at 5:00pm loves mozzarella."
  [Candidate (= ?id id) (= ?five five) (= ?mozzarella mozzarella)]
  [:test (not (= ?five ?mozzarella))]
  =>
  (insert! (->Rulefail "rule-11" ?id)))






;;; Queries
(defquery get-rulefail
  "Get failed rules"
  []
  [?rulefail <- Rulefail])



;;; Session and fire
(def rulefails
  (->> (let [sess-init (mk-session)]
         (for [cand cands]
           (let [sess-fact (fire-rules (insert sess-init cand))]
             (->> (query sess-fact get-rulefail)
                  (map :?rulefail)))))
       (apply concat)))

;;; Extract failed rules from cands
(->>
  (let [rulefail-ids (set (map :id rulefails))]
    (filter #(rulefail-ids (:id %)) cands))
  (take 3)
  (map #(println-str % "\n"))
  println)

