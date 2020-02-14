(ns clara-rules.movie-rules
  "This is a basic example showing an intuitive example of simple rules, facts and queries.

  The case is that of a movie theater, and the rules are about who can get tickets to which movies based on the movie rating age restrictions.

  G movies are for everybody, PG-13 movies are for those over 13 with an adult, and NC-17 means no one under 17 allowed, even with parent.
  "
  (:require [clara.rules :refer :all]))





;;; Establish fact record types and facts
;;;
;;; These are the fact types that go into the session that can be queried.  We
;;; need facts about the people attending and about the movies with their
;;; ratings.

;; A person is a potential attendee, and whose relevant properties are name (for identification), age, and with-parent status.
(defrecord Person [name age with-parent])

;; A vector of people who want to see movies
(def people [
             (->Person :alice  8 true)
             (->Person :bob   12 false)
             (->Person :carol 16 false)
             (->Person :dave  16 true)
             (->Person :ellen 20 false)
             ])



;; A movie has a name and a rating
(defrecord Movie [name rating])

;; A vector of movies
(def movies [
            (->Movie "Paint Dries" :G)
            (->Movie "Kids Who Swear Sometimes" :PG-13)
            (->Movie "Too Much Sex and Violence" :NC-17)
            ])



;; A ticket to a movie.  Matches up a person with a movie.
;; Isn't created at initialization, but is created in the session when rules
;; are triggered/satisfied.

(defrecord Ticket [person-name movie-name])






;;; Make rules about attending movies
;(defrule make-ticket
;  "Rule that will make a ticket for a person to attend a movie if they pass the age and with-parent rule."
;  ;; First two lines goes and gets a person and a movie, and plucks out their attributes into variables, which are the identifiers that start with `?`.
;  ;; This looks like just a destructuring, but are actually conditions that
;  ;; need to be met.  There needs to be a person and movie fact in the session
;  ;; to even get a match.
;  [Person (= ?person-name name) (= ?age age) (= ?with-parent with-parent)]
;  [Movie (= ?movie-name name) (= ?rating rating)]

;  ;; Once a person and movie is matched (that is, a person/movie combinatin is being considered), that paring needs to pass a test.
;  ;; - If movie is rated :G, anyone can go.
;  ;; - If movie is rated :PG-13, person can go if they are 13 or older, or if they are with a parent.
;  ;; - If movie is rated :NC-17, you can only go if you are 17 or older.
;  [:test (or
;           ;; Movie is G
;           (= ?rating :G)
;           (and
;             (= ?rating :PG-13)
;             (or (> ?age 12)
;                 ?with-parent))
;           (and
;             (= ?rating :NC-17)
;             (> ?age 16))
;           )]

;  =>

;  (insert! (->Ticket ?person-name ?movie-name)))



(defrule rule-g
  "Rule to attend G-rated movie."
  ;; First two lines goes and gets a person and a G-rated movie, and plucks out their attributes into variables, which are the identifiers that start with `?`.
  ;; This looks like just a destructuring, but are actually conditions that
  ;; need to be met.  There needs to be a person and movie fact in the session
  ;; to even get a match.
  [Person (= ?person-name name) (= ?age age) (= ?with-parent with-parent)]
  [Movie (= ?movie-name name) (= :G rating)]

  ;; Once a person and movie is matched (that is, a person/movie combinatin is being considered), that paring needs to pass a test.
  ;; - If movie is rated :G, anyone can go, so no extra age or with-parent test needs to be performed.
  =>

  ;; RHS of rule, inserts a ticket that matches person with movie.
  (insert! (->Ticket ?person-name ?movie-name)))



(defrule rule-pg13
  "Rule to attend PG-13-rated movie."
  ;; First two lines goes and gets a person and a PG-13-rated movie, and plucks out their attributes into variables, which are the identifiers that start with `?`.
  [Person (= ?person-name name) (= ?age age) (= ?with-parent with-parent)]
  [Movie (= ?movie-name name) (= :PG-13 rating)]

  ;; Once a person and movie is matched (that is, a person/movie combinatin is being considered), that paring needs to pass a test.
  ;; - If movie is rated :PG-13, person can go if they are 13 or older, or if they are with a parent.
  [:test (or
           (> ?age 12)
           ?with-parent)]

  =>

  ;; RHS of rule, inserts a ticket that matches person with movie.
  (insert! (->Ticket ?person-name ?movie-name)))




(defrule rule-nc17
  "Rule to attend NC-17-rated movie."
  ;; First two lines goes and gets a person and a PG-13-rated movie, and plucks out their attributes into variables, which are the identifiers that start with `?`.
  [Person (= ?person-name name) (= ?age age) (= ?with-parent with-parent)]
  [Movie (= ?movie-name name) (= :NC-17 rating)]

  ;; Once a person and movie is matched (that is, a person/movie combinatin is being considered), that paring needs to pass a test.
  ;; - If movie is rated :PG-13, person can go if they are 13 or older, or if they are with a parent.
  [:test (> ?age 16)]

  =>

  ;; RHS of rule, inserts a ticket that matches person with movie.
  (insert! (->Ticket ?person-name ?movie-name)))





;;;
;;; Make session, insert facts, fire rules, make a query.
;;;


;; Query: What are all of the tickets issued?
(defquery all-tickets
  "Get all tickets issued"
  []
  [?tickets <- Ticket])

;; Make session with rules and queries, insert facts, execute query.
(let [sess (-> (mk-session)
               (insert-all people)
               (insert-all movies)
               fire-rules)
      myquery (query sess all-tickets)]
  ;; Basic query that will work, but adding stuff to it to make it more easily printable
  ; (println myquery)
  (map (comp println (partial into {}) :?tickets) myquery))





;;; Query: Movies that a given person can attend
;;; Adds a query that can take a parameter.  In this case, it is the name of a person for whom you want to see the available tickets.
(defquery person-tickets
  "What tickets are available to a person?"
  [:?person-name]
  [?tickets <- Ticket (= person-name ?person-name)])

;; Make session with rules and queries, insert facts, execute query.
(let [sess (-> (mk-session)
               (insert-all people)
               (insert-all movies)
               fire-rules)
      myquery (query sess person-tickets :?person-name :alice)]
  ;; Basic query that will work, but adding stuff to it to make it more easily printable
  ; (println myquery)
  (map (comp println (partial into {}) :?tickets) myquery))

