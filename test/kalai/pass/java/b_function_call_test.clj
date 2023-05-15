(ns kalai.pass.java.b-function-call-test
  (:require
   [clojure.test :refer [deftest is]]
   [kalai.pass.java.b-function-call :as b-function-call]))

(deftest java.b-function-call-test
  (is
   (=
    '(j/class
      test-package.test-class
      (j/block
       (j/function f []
                   (j/block
                    (j/block
                     (j/init x 0)
                     (j/block
                      (j/expression-statement (j/invoke println x)) ; does not expand println?
                      (j/expression-statement (j/return (j/operator + x 1)))))))))
    (b-function-call/rewrite
     '(j/class
       test-package.test-class
       (j/block
        (j/function f []
                    (j/block
                     (j/block
                      (j/init x 0)
                      (j/block
                       (j/expression-statement (j/invoke println x))
                       (j/expression-statement (j/return (j/operator + x 1)))))))))))))
