(ns clara-lein.ex06
  "Main ns to run the query from."
  (:require 
    [clara-lein.ex06facts :as facts]
    [clara-lein.ex06queries :refer [get-rulefail]]
    [clara.rules :refer :all]))


;;; Facts are in clara-ns-ex.facts ns
;;; Rules are in clara-ns-ex.rules ns
;;; Queries are in clara-ns-ex.queries
(let [init-sess (mk-session 'clara-lein.ex06rules 'clara-lein.ex06queries)
      fact-sess (insert-all init-sess facts/facts)]
  (-> fact-sess
      fire-rules
      (query get-rulefail)))


