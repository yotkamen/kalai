(ns kalai.pass.shared.flatten-groups-test
  (:require [clojure.test :refer [deftest is]]
            [kalai.pass.shared.flatten-groups :as flatten-groups]))

(deftest flatten-group-test
  (is (=
       '(j/block
         (j/init x 1)
         (j/init x 3)
         (j/init y 2)
         (j/init z 1))
       (flatten-groups/rewrite
        '(j/block
          (group (j/init x 1)
                 (j/init x 3))
          (group (j/init y 2))
          (group (j/init z 1)))))))
