(ns kalai.pass.shared.raise-stuff-test
  (:require [clojure.test :refer [deftest is]]
            [kalai.pass.shared.raise-stuff :as raise-stuff]))

(deftest raise-stuff-test
  (is (=
       '(j/class test-package.test-class
                 (j/block
                  (j/function test-function []
                              (j/block
                               (j/block
                                (group (j/init x true))
                                (group (j/init x true))
                                (j/block
                                 (group (j/init z true))
                                 nil)
                                (group (j/init y 5)))))))
       (raise-stuff/rewrite
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
