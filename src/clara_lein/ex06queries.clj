(ns clara-lein.ex06queries
  "Define queries to use.  Needs some local record defs."
  (:require 
    [clara-lein.ex06rules]
    [clara.rules :refer :all])
  (:import 
    (clara_lein.ex06rules Rulefail)))

(defquery get-rulefail
  "Get failed rules"
  []
  [?rulefail <- Rulefail])

