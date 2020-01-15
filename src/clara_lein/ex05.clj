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
;;;; See: http://blog.jenkster.com/2013/02/solving-logic-puzzles-with-clojures-corelogic.html
;;;; See: https://gist.githubusercontent.com/Engelberg/5105806/raw/8062f885a8ac01e5d0a4c916202fd9a26bb4c942/Logic1.clj
;;;;
;;;; THE CONSTRAINTS IN PLAIN ENGLISH            
;;;;        Of Landon and Jason, one has the 7:30pm reservation and the other loves mozzarella.
;;;;        The blue cheese enthusiast subscribed to Fortune.
;;;;        The muenster enthusiast didn't subscribe to Vogue.
;;;;        The 5 people were the Fortune subscriber, Landon, the person with a reservation at 5:00pm, the mascarpone enthusiast, and the Vogue subscriber.
;;;;        The person with a reservation at 5:00pm didn't subscribe to Time.
;;;;        The Cosmopolitan subscriber has an earlier reservation than the mascarpone enthusiast.
;;;;        Bailey has a later reservation than the blue cheese enthusiast.
;;;;        Either the person with a reservation at 7:00pm or the person with a reservation at 7:30pm subscribed to Fortune.
;;;;        Landon has a later reservation than the Time subscriber.
;;;;        The Fortune subscriber is not Jamari.
;;;;        The person with a reservation at 5:00pm loves mozzarella.
;;;; 
;;;; The main strategy to this is:
;;;;
;;;; - Track a candidate configuration as a record, which is essentially a map.
;;;;
;;;;     The map fields are:
;;;;       id: a numeric id that labels a configuration candidate.
;;;;       fortune time cosmopolitan us-weekly vogue: the magazines
;;;;       asiago blue-cheese mascarpone mozzarella muenster: the cheeses
;;;;       five six seven seven-thirty eight-thirty: the reservation times.
;;;;     The map values are the people:
;;;;       amaya bailey jamari jason landon
;;;;     
;;;;     Each candidate has an id and a permutation of the people assigned to
;;;;     the field groups:
;;;;       magazine, cheese, and reservation time.
;;;;
;;;;  An example candidate is:
;;;; #clara_lein.ex05.Candidate{:id 0,
;;;;    :fortune :amaya, :time :bailey, :cosmopolitan :jamari, :us-weekly :jason, :vogue :landon,
;;;;    :asiago :amaya, :blue-cheese :bailey, :mascarpone :jamari, :mozzarella :jason, :muenster :landon,
;;;;    :five :amaya, :six :bailey, :seven :jamari, :seven-thirty :jason, :eight-thirty :landon}
;;;;
;;;; Note how each line for magazine, cheese, and time has a permutation of the people.
;;;;
 


;;;; Symbols that will be used
;;;; Have: people, magazines, cheeses, and reservation times
(def people
  '(amaya bailey jamari jason landon))

(def mag-syms
  '(fortune time cosmopolitan us-weekly vogue))

(def cheese-syms
  '(asiago blue-cheese mascarpone mozzarella muenster))

(def time-syms
  '(five six seven seven-thirty eight-thirty))


;; Put the time keywords in order
;(def time-keywords
;  (map keyword time-syms))





;;; This constructs a record that has all of the symbols, plus an `id` field
;;; that has a unique identifying number.
;;;
;;; Use quoting and evaluation to save typing and use the syms definitions to get the record.
;;;

;; slack solution in beginner's channel
;; https://clojurians.slack.com/archives/C053AK3F9/p1578796869371900
(eval
  `(~'defrecord ~'Candidate [~'id ~@mag-syms ~@cheese-syms ~@time-syms])) 
 
;; My solution, fwiw, suboptimal.
;;(eval (list 'defrecord 'Candidate (vec (reduce concat (list (list 'id) mag-syms cheese-syms time-syms))))) 


;;;; Make a seq of all candidates
;;;;
;;;; This is one with a subset of candidates.
;;;; Time it to see how long it takes to construct.
;;;;
;;;; It's slow, but the point of this exercise is not speed.
(time
  (def cands
    (let [pkeyperm (-> (map keyword people) permutations)
          mkey (map keyword mag-syms) 
          ckey (map keyword cheese-syms)
          tkey (map keyword time-syms)
          cand-maps (for [mpval pkeyperm
                          cpval pkeyperm
                          tpval pkeyperm]
                      (zipmap (concat mkey ckey tkey) (concat mpval cpval tpval)))
          ;; Short version, for quick testing
          ;cand-short (take 999 cand-maps)
          ;; Full version
          cand-short cand-maps
          ]
      (map map->Candidate (map #(into %1 {:id %2}) cand-short (range (count cand-short)))))))
;; On my machine:
;; Elapsed time: 11507.3016 msecs

;; Count total number of candidate permutations
(time (count cands))
;; On my machine:
;; count: 1728000 records
;; Elapsed time: 5730.9774 msecs





;;; Rule will insert a record if the rule condition fails on a candidate
;;; Define record for a rule failure.  Captures the rule-id of the failed rule and the id of the candidate tested.
(defrecord Rulefail [rule-name id])


;;;; Rules
;;;;
;;;; Rules are implemented in the logical negation of the given rule.  That is
;;;; so that the rule is triggered if a violation of the rule occurs for
;;;; a candidate.

;; Here I use the boolean rule forms to trigger the rule.
(defrule rule-1
  "Of Landon and Jason, one has the 7:30pm reservation and the other loves mozzarella."
  [Candidate (= ?id id)]
  [:not [:or
          [Candidate (= ?id id) (= seven-thirty :landon) (= mozzarella :jason)]
          [Candidate (= ?id id) (= seven-thirty :jason) (= mozzarella :landon)]]]
  =>
  (insert! (->Rulefail "rule-1" ?id)))


;; Here I use a :test on the candidate.
(defrule rule-2
  "The blue cheese enthusiast subscribed to Fortune"
  [Candidate (= ?id id) (= ?fortune fortune) (= ?blue-cheese blue-cheese)]
  [:test (not= ?fortune ?blue-cheese)]
  =>
  (insert! (->Rulefail "rule-2" ?id)))


;; Again, as this is rule violation, for this rule, the rule fails if the
;; person who reads vogue aslo likes muenster.
(defrule rule-3
  "The muenster enthusiast didn't subscribe to Vogue"
  [Candidate (= ?id id) (= ?vogue vogue) (= ?muenster muenster)]
  [:test (= ?vogue ?muenster)]
  =>
  (insert! (->Rulefail "rule-3" ?id)))


;; This fails if the candidate doesn't have 5 separate people with these attributes.
(defrule rule-4
  "The 5 people were the Fortune subscriber, Landon, the person with a reservation at 5:00pm, the mascarpone enthusiast, and the Vogue subscriber."
  [Candidate (= ?id id) (= ?fortune fortune) (= ?five five) (= ?marscapone mascarpone) (= ?vogue vogue)]
  [:test (not= 5 (count (set [?fortune :landon ?five ?marscapone ?vogue])))]
  =>
  (insert! (->Rulefail "rule-4" ?id)))


;; Fires Rulefail if Time subscriber is same as five oclock reservation.
(defrule rule-5
  "The person with a reservation at 5:00pm didn't subscribe to Time."
  [Candidate (= ?id id) (= ?five five) (= ?time time)]
  [:test (= ?time ?five)]
  =>
  (insert! (->Rulefail "rule-5" ?id)))


;; This one is more complex, as it deals with reservation time order.  The ugly
;; part is that I have to unify all of the time fields for checking.  Further,
;; I have to order the reservations field, and then check the other fields and
;; whether or not they come earlier or later in the reservation order.
(defrule rule-6
  "The Cosmopolitan subscriber has an earlier reservation than the mascarpone enthusiast."
  [Candidate (= ?id id) (= ?cosmopolitan cosmopolitan) (= ?mascarpone mascarpone) (= ?five five) (= ?six six) (= ?seven seven) (= ?seven-thirty seven-thirty) (= ?eight-thirty eight-thirty)]
  [:test (let [reservations [?five ?six ?seven ?seven-thirty ?eight-thirty]
               cosmopolitan-index (.indexOf reservations ?cosmopolitan)
               mascarpone-index (.indexOf reservations ?mascarpone)]
           (not (< cosmopolitan-index mascarpone-index)))]
  =>
  (insert! (->Rulefail "rule-6" ?id)))


;; Again, have t account for time order of reservations.
(defrule rule-7
  "Bailey has a later reservation than the blue cheese enthusiast."
  [Candidate (= ?id id) (= ?blue-cheese blue-cheese) (= ?five five) (= ?six six) (= ?seven seven) (= ?seven-thirty seven-thirty) (= ?eight-thirty eight-thirty)]
  [:test (let [reservations [?five ?six ?seven ?seven-thirty ?eight-thirty]
               bailey-index (.indexOf reservations :bailey)
               blue-cheese-index (.indexOf reservations ?blue-cheese)]
           (not (< blue-cheese-index bailey-index)))]
  =>
  (insert! (->Rulefail "rule-7" ?id)))


;; This could be done like rule-1 with [:not [:or ...], but doing this way for variety.
(defrule rule-8
  "Either the person with a reservation at 7:00pm or the person with a reservation at 7:30pm subscribed to Fortune."
  [Candidate (= ?id id) (= ?fortune fortune) (= ?seven seven) (= ?seven-thirty seven-thirty)]
  [:test (not (or
           (= ?fortune ?seven)
           (= ?fortune ?seven-thirty)))]
  =>
  (insert! (->Rulefail "rule-8" ?id)))


;; Reservation time order again.
(defrule rule-9
  "Landon has a later reservation than the Time subscriber."
  [Candidate (= ?id id) (= ?time time) (= ?five five) (= ?six six) (= ?seven seven) (= ?seven-thirty seven-thirty) (= ?eight-thirty eight-thirty)]
  [:test (let [reservations [?five ?six ?seven ?seven-thirty ?eight-thirty]
               landon-index (.indexOf reservations :landon)
               time-index (.indexOf reservations ?time)]
           (not (< time-index landon-index)))]
  =>
  (insert! (->Rulefail "rule-9" ?id)))

;; Simple rule
(defrule rule-10
  "The Fortune subscriber is not Jamari."
  [Candidate (= ?id id) (= ?fortune fortune)]
  [:test (not (= ?fortune :jamari))]
  =>
  (insert! (->Rulefail "rule-10" ?id)))

;; Simple rule
(defrule rule-11
  "The person with a reservation at 5:00pm loves mozzarella."
  [Candidate (= ?id id) (= ?five five) (= ?mozzarella mozzarella)]
  [:test (not (= ?five ?mozzarella))]
  =>
  (insert! (->Rulefail "rule-11" ?id)))






;;; Query
;;;
;;; Simple query is to pull all of the Rulefail facts
(defquery get-rulefail
  "Get failed rules"
  []
  [?rulefail <- Rulefail])



;;; Session and fire
;;;
;;; Strategy here is to loop over candidates and insert them into a fresh
;;; session, fire the rules, and collect the Rulefail facts.
(time
  (def rulefails
    (->> (let [sess-init (mk-session)]
           (for [cand cands]
             (let [sess-fact (fire-rules (insert sess-init cand))]
               (->> (query sess-fact get-rulefail)
                    (map :?rulefail)))))
         (apply concat))))
;; Elapsed Time: 44.8767 msecs


;; How many total Rulefail facts are recorded, and how long did it take?
(time
  (count rulefails))
;; 12,220,416
;; Elapsed Time: 222204.2446 msecs
;; Or 222 seconds, or about 3.5 minutes


;; Look at some sample failed rules
(take 3 rulefails)
;; (#clara_lein.ex05.Rulefail{:rule-name "rule-1", :id 0}
;; #clara_lein.ex05.Rulefail{:rule-name "rule-2", :id 0}
;; #clara_lein.ex05.Rulefail{:rule-name "rule-3", :id 0})





;;; Get the fail-ids
;;; From the failed rules, extract set of all ids that had at least 1 rulefail.
(def fail-ids
  (->>
    (let [rulefail-ids (set (map :id rulefails))]
      (filter #(rulefail-ids (:id %)) cands))
    (map :id)
    set))

(count fail-ids)
;; ids with failures: 1,727,999
;;
;; That's one less than total number of candidates, which is good!
;; There is then one solution (the rest are failures.)

;;; Extract failed rules from cands
(->>
  (let [rulefail-ids (set (map :id rulefails))]
    (filter #(rulefail-ids (:id %)) cands))
  (take 3)
  (map #(println-str % "\n"))
  println)


;;; Get the sucessful record
(time
  (def soln-id-set
    (clojure.set/difference (set (range 1728000)) fail-ids)))
(first soln-id-set)
;; 738075

;; Get that record from the candidates
(filter #(= (:id %) (first soln-id-set)) cands)
;; (#clara_lein.ex05.Candidate{:id 738075,
;;    :fortune :jamari, :time :amaya, :cosmopolitan :jason, :us-weekly :landon, :vogue :bailey,
;;    :asiago :bailey, :blue-cheese :jamari, :mascarpone :amaya, :mozzarella :jason, :muenster :landon,
;;    :five :jason, :six :amaya, :seven :jamari, :seven-thirty :landon, :eight-thirty :bailey})

