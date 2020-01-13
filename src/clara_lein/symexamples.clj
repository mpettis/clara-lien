(ns clara-lein.symexamples
  (:require [clara.rules :refer :all]
            [clojure.math.combinatorics :refer :all]))

;;;; Small steps to construct data structures from symbols

;;; Define some syms
(def xsyms '(x1 x2 x3))
(def ysyms '(y1 y2 y3))
(def zsyms '(z1 z2 z3))


;;; Turn syms to keywords
(map keyword xsyms)

;;; Concat symbol lists
(concat xsyms ysyms)

;;; Turn xsyms int keywords and make permutations
(->> xsyms (map keyword) permutations)

;;; Turn seq of lists into flattened list
(->> xsyms (map keyword) permutations (reduce concat))

;;; Assign permutations to keywords.
;;; This assigns kewords to symbols.
;;; Use xsyms and assign ysyms (as keywords)
(for [xsyms (->> ysyms (map keyword) permutations)]
  xsyms)

;;; Concat syms
(for [ysyms (->> xsyms (map keyword) permutations)
      zsyms (->> xsyms (map keyword) permutations)]
  (concat ysyms zsyms))

;;; Combine syms and keyvals, single vector
(let [ykey (map keyword ysyms)]
  (for [yxval (->> xsyms (map keyword) permutations)]
    (zipmap ykey yxval)))

;;; Combine keywords and values, multiple groups
(let [ykey (map keyword ysyms) 
      zkey (map keyword zsyms)]
  (for [yxval (-> (map keyword xsyms) permutations)
        zxval (-> (map keyword xsyms) permutations)]
    (zipmap (concat ykey zkey) (concat yxval zxval))))

;;; Combine keywords and values, multiple groups, version 2, compact.
(let [xkeyperm (-> (map keyword xsyms) permutations)
      ykey (map keyword ysyms) 
      zkey (map keyword zsyms)]
  (for [yxval xkeyperm
        zxval xkeyperm]
    (zipmap (concat ykey zkey) (concat yxval zxval))))



