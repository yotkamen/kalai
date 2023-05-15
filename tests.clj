(ns kalai.pass.tests
  (:require [kalai.exec.kalai-to-language :as t]
            [kalai.pass.kalai.pipeline :as kalai-pipeline]
            [kalai.exec.kalai-to-language :as k]
            [kalai.pass.java.pipeline :as java-pipeline]
            [kalai.pass.excel.pipeline :as excel-pipeline]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn as-ns [form]
  (list '(ns test-package.test-class)
        form))

(defn as-function [form]
  (list '(ns test-package.test-class)
        (list 'defn 'test-function ^{:t :void} [] form)))

(defn sumif
  ([^{:t :vector} range
    criterion
    ^{:t vector-long } sum-range]
   (reduce
    +
    (map #(if (criterion %1) %2 0)
         range
         sum-range)))
  ([range criterion]
   (sumif range criterion range)))

(#(->> %
       (kalai.pass.shared.flatten-groups/rewrite)
       (kalai.pass.excel.a-syntax/rewrite)
       (kalai.pass.excel.e-string/stringify-entry)
       )
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (as-function
    '(sumif )))))

(let [range [1 2 3 3]
      criterion #(> % 2)
      sum-range [20 30 40]]
  (sumif range criterion sum-range))

'(sumif [1 2 3] #(> % 2))
'(map #(* % 2) [1 2 3] 'c1 'c 3)
'(map (fn [aa1 aa2] (+ 4 (* aa2 aa1 5))) 'c1)
'(map - 'c1)
'(+ 1
   2
   (* 3
      (reduce + (map - [1 2 3]))
      (+ ((fn [aa1 aa2] (+ 4 (* aa2 aa1 5)))
          6 7)
         11)))
'(let [x1 (#(+ 4 (* %2 %1 5))
            6 7) ]
       x1)
'(+ 1 2 (* 3 #(+ 1 %)))
''(#(+ 1 %) 43)


(excel-pipeline/kalai->excel
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (as-ns
    '(def ^Integer x (+ 1 2))))))

; value:
;   example: sheet!B4
; collection:
;   example: sheet!A1:B4
;   example: sheet!B:B

; representation:
;   cell
;   column

; variable:
;   'coef'!B5
;   (sheet cell)
; array 1-dimension not limitted:
;   'form'!A:A
;   (sheet col1 col1)
; array 1-dimension limitted:
;   'form'!A0:A10
;   (sheet col1Index1:col1Index2)
; array N-dimension limitted:
;   'form'!A0:A10
;   (sheet col1Index1:col3Index2)
;=MULTIPLY('coef'!B5; SUMIFS('form'!B:B; MAP('form'!A:A; LAMBDA(TIMESTAMP; YEAR(TIMESTAMP))); 2023; 'form'!C:C; "чр"))
;
; SUMIFS('form'!B:B; MAP('form'!A:A; LAMBDA(TIMESTAMP; YEAR(TIMESTAMP))); 2023; 'form'!C:C; "чр")
; (reduce (comp #(f1 colAA) #(f2 colCC)) colBB)

;=MAP('form'!A:A; LAMBDA(TIMESTAMP; YEAR(TIMESTAMP)))
; (mapv #(year %) col)

;=LAMBDA(TIMESTAMP; YEAR(TIMESTAMP))
; #(YEAR %)
; #(f %)
;(* coef-b5 (

(java-pipeline/kalai->java
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (as-ns
    '(def ^Integer x)))))

(java-pipeline/kalai->java
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (as-function
    '(let [^{:t :int} x (int 1)]
       x)))))

(kalai.pass.java.a-syntax/rewrite
  (t/analyze-forms
   (as-function
    '(let [y (int 34)
           a (#(+ 1 %) 43)
           z 3]
       z))))

(java-pipeline/kalai->java
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (as-function
    '(let [^Integer x (Integer. (int 1))
           x1 (Integer. (int 1))
           y (int 34)
           a (#(+ 1 %) (new Integer 1))
           a (#(fn [x] + 1 x) (new Integer 10000))
           z 3]
       x)))))

(require '[kalai.util :as u]
         '[meander.strategy.epsilon :as s]
         '[meander.epsilon :as m]
         '[meander.strategy.epsilon :as r])

(java-pipeline/kalai->java
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (list '(ns test-package.test-class)
         '(defn f ^{:t :int} []
            (let [y (* 1 2 3)
                  x {:a 2 :b (reduce + (map - [1 2 3])) }]
              x))))))

(java-pipeline/kalai->java
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (as-ns
    '(defn f ^Long [^Long x]
       (inc x))))))

(java-pipeline/kalai->java
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   `(~as-ns
      (defn f ^{:t :int} []
        (let [x (atom (int 0))]
          (reset! x (int 2))
          (println x)
          x))))))

(java-pipeline/kalai->java
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (list '(ns test-package.test-class)
         '(defn f ^{:t :int} []
            (let [x (atom (int 0))]
              (reset! x (int 2))
              (println x)
              x))))))

(java-pipeline/kalai->java
 (k/read-kalai (io/file "examples/src/a/demo02.clj")))
; (k/read-kalai (io/file "test/kalai/pass/java_test.clj"))

(k/read-kalai (io/file "examples/src/b/simple.clj"))

(java-pipeline/kalai->java
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (as-function
    '(let [x (atom (int 1))
           y (atom (int 2))
           ^int a (atom (long 2))
           z (int 1)]
       (reset! x (int 3))
       (println (+ @x (deref y))))))))

(java-pipeline/kalai->java
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (as-function
    '(do (def ^{:t :bool} x true)
         (def x true)
         (let [z true]
           z)
         (def ^{:t :long} y 5))))))

(java-pipeline/kalai->java
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (identity
    '((ns test-package.test-class)
      (defn f
        (^{:t :int} [^{:t :int} x]
         (+ x (int 1)))
        (^{:t :int} [^{:t :int} x ^{:t :int} y]
         (+ x y))))))))

(java-pipeline/kalai->java
 (kalai-pipeline/asts->kalai
  (t/analyze-forms
   (identity
    '((ns test-package.test-class)
      (def ^{:kalias {:mmap [:long :string]}} T)
      (def ^{:t T} x)
      (defn f ^{:t T} [^{:t T} y]
        ^{:t T} {1 "hahaha"}))))))

(defn grab-all-foods [user]
  (m/find user
    {:favorite-foods [{:name !foods} ...]
     :special-food !foods
     :recipes [{:title !foods} ...]
     :meal-plan {:breakfast [{:food !foods} ..1]
                 :lunch [{:food !foods} ...]
                 :dinner [{:food !foods} ...]}}
    !foods))

(grab-all-foods
  {:favorite-foods [{:name :nachos}
                    {:name :smoothie}]
   :special-food :meatballs
   :recipes [{:title :soup}  ]
   :meal-plan {:breakfast [{:food :nachosi} {:food :parmesano}]
               :lunch [{:food :nachosi}]
               :dinner [{:food :nachosi}]}

  })

(m/match '((+ 1 1) 2 1 (+ 3 i 4))
    (?x . !more ... :as ?form)
  !more)
_#

(m/find [1 "" ]
  [_# ?y] ?y
  [_ (m/pred number? ?y) _] ?y)

(m/defsyntax number
  ([] `(number _#))
  ([pattern]
    (if (m/match-syntax? &env)
      `(m/pred number? ~pattern)
      &form)))

(m/defsyntax number
  ([] `(number _#))
  ([pattern] `(m/pred number? ~pattern)))

(m/find [1 2 3]
  [(number)  (number ?y)]
  ?y

  [(number) (number ?y) (number)]
  ?y)

(m/match :foo/bar
  (m/pred ident? (m/app namespace ?ns) (m/app name ?name))
  [?ns ?name]

  _
  nil)

(namespace :user/bar)

(name :user/bar)

((r/build "shoe") nil)
(let [s (r/pipe inc dec #(+ 1 %) )]
  (s 10))
(let [s (r/pipe (r/pred even?) inc)]
  (s 3))
(let [s (r/find
          {:ns ?ns
           :namespaces {?ns ?syms}}
          ?syms)]
  (s '{:ns b.core
       :namespaces {a.core [a aa aaa]
                    b.core [b bb bbb]}}))
(let [s (r/find
          {:ns ?ns
           :namespaces {?ns ?syms}}
          ?syms)]
  (s '{:ns c.core
       :namespaces {a.core [a aa aaa]
                    b.core [b bb bbb]}}))
(let [s (r/rewrite
         (let* [!bs !vs ..1]
           . !body ...)
         (let* [!bs !vs]
           (let* [!bs !vs ...]
             (spam  !body ...))))]
    (s '(let* [b1 :v1, b2 :v2, b3 :v3]
          (vector b1 b2 c3)(vector b1 b2 b3))))
