(ns
    kalai.pass.java.c-condense-test
  (:require
   [clojure.test :refer [deftest is]]
   [kalai.pass.java.c-condense :as c-condense]))

(deftest java.c-condense-test
  (is
   (=
    '(j/class
      test-package.test-class
      (j/block
       (j/function test-function []
                   (j/block
                    (j/init x true)
                    (j/init x true)
                    (j/block (j/init z true) nil)
                    (j/init y 5)))))
    (c-condense/rewrite
     '(j/class
       test-package.test-class
       (j/block
        (j/function test-function []
                    (j/block
                     (j/block
                      (j/init x true)
                      (j/init x true)
                      (j/block (j/init z true) nil)
                      (j/init y 5))))))))))
