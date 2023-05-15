(ns kalai.pass.java.ab-cast-test
  (:require [clojure.test :refer [deftest is]]
            [kalai.pass.java.ab-cast :as java.ab-cast]))

(deftest java-ab-cast-test
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
       (java.ab-cast/rewrite
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
                                 (j/init y 5))))))))))
