(require '[meander.epsilon :as m])

(defn favorite-food-info [foods-by-name user]
  (m/match {:user user
            :foods-by-name foods-by-name}
    {:user
     {:name ?name
      :favorite-food {:name ?food}}
     :foods-by-name {?food {:popularity ?popularity
                            :calories ?calories}}}
    {:name ?name
     :favorite {:food ?food
                :popularity ?popularity
                :calories ?calories}}))

(def foods-by-name
  {:nachos {:popularity :high
            :calories :lots}
   :smoothie {:popularity :high
              :calories :less}})

(favorite-food-info foods-by-name
                    {:name :alice
                     :favorite-food {:name :nachos}})

(m/match {:user
          {:name :alice
           :favorite-food {:name :nachos}}
          :foods-by-name {:nachos {:popularity :high
                                   :calories :lots}
                          :smoothie {:popularity :high
                                     :calories :less}}}
  {:user
   {:name ?name
    :favorite-food {:name ?food}}
   :foods-by-name {?food {:popularity ?popularity
                          :calories ?calories}}}
  {:name ?name
   :favorite {:food ?food
              :popularity ?popularity
              :calories ?calories}})

(m/search {:user
           {:name :alice
            :favorite-foods [{:name :smoothie}{:name :nachos}]}
           :foods-by-name {:nachos {:popularity :high
                                    :calories :lots}
                           :smoothie {:popularity :high
                                      :calories :less}}}
  {:user
   {:name ?name
    :favorite-foods (m/scan {:name ?food})}
   :foods-by-name {?food {:popularity ?popularity
                          :calories ?calories}}}
  {:name ?name
   :favorite {:food ?food
              :popularity ?popularity
              :calories ?calories}})


(m/defsyntax number
  ([] `(number _#))
  ([pattern]
    (if (m/match-syntax? &env)
      `(m/pred number? ~pattern)
      &form)))

(m/find point
  [(number) (number ?y)]
  ?y

  [(number) (number ?y) (number)]
  ?y)

(require '[clojure.string :as str])

(require '[meander.strategy.epsilon :as s])

(require '[meander.epsilon :as m])

(defn initOppath-clj [axpath]
    (let [nsandpath (str/split axpath #"[:]" 2)
          nsstr (first nsandpath)
          pathtokens (->
                      nsandpath
                      (nth 1)
                      (str/split #"[/]"))]
      {:ns (case nsstr
             "op"    :op
             "obj"   :obj
             "oppas" :oppas)
       :xsegs (map
               #(if (= (first %1) \@)
                  (->m/OppathSeg :seg-attr %1)
                  (->OppathSeg :seg-chld %1))
               pathtokens)}))

(println (kalai-pipeline/asts->kalai (t/analyze-forms (as-ns '(def ^{:t :int} x (int 3))))))
(t/analyze-forms (as-ns '(def ^{:t :int} x (int 3))))


(kalai-pipeline/asts->kalai (t/analyze-forms (as-ns '(def ^{:t :int} x (int 3)))))

(j/class test-package.test-class
         (j/block
          (j/function test-function []
                      (j/block
                       (j/block
                        (group (j/init x 1))
                        (group (j/init y 2))
                        (group (j/init z 1))
                        (j/block
                         (group (j/assign x 3))
                         (group (j/expression-statement
                                 (j/invoke println (j/operator + x y))))))))))

(j/class test-package.test-class
         (j/block
          (j/function test-function []
                      (j/block
                       (j/block
                        (j/init x 1)
                        (j/init y 2)
                        (j/init z 1)
                        (j/block
                         (j/assign x 3)
                         (j/expression-statement
                          (j/invoke println (j/operator + x y)))))))))
