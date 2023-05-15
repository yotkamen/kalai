(ns kalai.pass.java.a-syntax-test
  (:require [clojure.test :refer [deftest is]]
            [kalai.pass.java.a-syntax :as java.a-syntax]))

(deftest java-a-syntax-test
  (is (=
       '(j/class test-package.test-class
                 (j/block
                  (j/function test-function []
                              (j/block
                               (j/block
                                (j/init x true)
                                (j/init x true)
                                (j/block
                                 (j/init z true)
                                 nil)
                                (j/init y 5))))))
       (java.a-syntax/rewrite
        '(namespace test-package.test-class
                    (function test-function []
                              (do
                                (init x true)
                                (init x true)
                                (do
                                  (init z true)
                                  z)
                                (init y 5))))))))


(deftest
 java-a-syntax-test
 (is
  (=
   '(j/class b.loops
     (j/block
      (j/function -main [& _args]
       (j/block
        (j/block
         (j/init i1 0)
         (j/while (j/operator < i1 10)
           (j/block
            (j/expression-statement (j/invoke println i1))
            (j/assign i1 (j/operator + i1 1))))
         (j/init i2 0)
         (j/while (j/operator < i2 10)
           (j/block
            (j/expression-statement (j/invoke println i2))
            (j/assign i2 (j/operator + i2 1))))
         (j/foreach ii
          (j/method addLast
           (j/method addLast
            (j/method addLast (j/new nil) 1)
            2)
           323)
          (j/block (j/expression-statement (j/invoke println ii))))
         (j/block
          (j/init x 0)
          (j/while (j/operator < x 10)
            (j/block
             (j/assign x (j/operator + x 1))
             (j/expression-statement (j/invoke println x))))))))))
   (java.a-syntax/rewrite
    '(namespace b.loops
     (function -main [& _args]
      (do
       (init i1 0)
       (while (operator < i1 10)
        (invoke println i1)
        (assign i1 (operator + i1 1)))
       (init i2 0)
       (while (operator < i2 10)
        (invoke println i2)
        (assign i2 (operator + i2 1)))
       (foreach ii [1 2 323] (invoke println ii))
       (do
        (init x 0)
        (while (operator < x 10)
         (assign x (operator + x 1))
         (invoke println x))))))))))
