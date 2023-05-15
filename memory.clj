(def memory {})

(defn allocate [location]
  (let [sheet :Sheet1
        sheet-location (assoc memory sheet (object-array location))]
    (str (name sheet) "!" "A:B")))

(allocate nil)

;;; allocate by sheet and 1 or 2 dimensial array
(assoc memory sheet (object-array [1 "kk" 3]))

;;; read cell
(let [sheet :Sheet1]
  (aget
   (get
    (assoc memory sheet (object-array [1 "kk" 3]))
    sheet)
   1))

;;; write cell
(let [sheet :Sheet1]
  (aset
   (get
    (assoc memory sheet (object-array [1 "kk" 3]))
    sheet)
   2
   "kjk"))

;;; write cell
(let [sheet :Sheet1]
  (aset
   (get
    (assoc memory sheet (object-array 3))
    sheet)
   1
   "kjk"))

;;; read vector
(let [sheet :Sheet1]
  (get
   (assoc memory sheet (object-array [1 "kk" 3]))
   sheet))

;;; read 2 dimensal array
(let [sheet :Sheet1]
  (aget
   (get
    (assoc memory sheet (object-array [1 "kk" 3]))
    sheet)
   1))

(def kk [1 2 3])
(def kk (object-array 3))
(aset kk 0 "kl")
(aget kk 0 1 )
(def hello (to-array-2d [kk]))
(aget (aget hello 0) 0)
(aset hello 0 kk)

(let [spam [1 2 3]]
  (reduce + spam)
  (contains? spam 10))

(type (to-array-2d [kk]))
(type (to-array [kk]))
(=
 (type (int-array 3))
 (type (object-array 3)))

(count (to-array-2d [[1 2 3] []]) )
(vector 1)
(count [1])

(defmacro aget2o [a i j]
  `(aget ^"[Ljava.lang.Object;" (aget ~a ~i) ~j))

(aget (to-array-2d [[1 2 3] []])
      0 5)


(aget2o (to-array [(to-array [1 2 3])(to-array [ 1 2 3]) ])
      1 2)

(aget ^"[Ljava.lang.String"
      (aget
       (to-array [(to-array [1 2 3])(to-array [ 1 2 3]) ])
       0)
      1)

(type 1)
(+ ^"[Ljava.lang.Object;"
   (+
    1
    0)
   1)

(type ^"java.lang.String"
   (+
    1
    0.0))

(defn array? [obj]
  (.isArray (class obj)))

(array? [3])
(value-type (object-array 3))
(value-type [1 2])

(defn value-type [obj]
  (cond (.isArray (class obj)) :array
        (coll? obj) :collection))
(nth (to-array {:a 1})  0 0)
(type (to-array {:a 1}))
(nth (aget (to-array {:a 1}) 0) 0)

(reduce into [] {:a 1})
(vec #{1 2})
(vec '(1 2))
(to-array-2d [(object-array [1 2])])

(defn composite? [obj]
  (if-not (nil? obj)
    (or
     (.isArray (class obj))
     (coll? obj))))

(composite? {})
(composite? [])
(composite? '())
(composite? "")
(composite? (first (object-array 3)))

(defn simple-collection? [coll]
  (println (str "enter simple-collection? with: " coll))
                                        ;  (print (str "first: " (first coll) " val: " (composite? (first coll))))
  (or
   (nil? coll)
   (if (.isArray (class coll))
     (zero? (count coll))
     (empty? coll))
   (if-not (composite? (first coll))
     (simple-collection? (rest coll)))))

(simple-collection? [1 2 [1 1 2] 3])
(simple-collection? #{1 2 [[2] 2] 3})
(simple-collection? [1 2])
(simple-collection? nil)
(simple-collection? (object-array 3))
(simple-collection? (object-array [1 [] 2]))
(simple-collection? (object-array 0))
(simple-collection? (to-array []))
(simple-collection? "iii")

(defn any-to-array-2d [obj]
  (cond
    (nil? obj)
    (to-array-2d [])

    (.isArray (class obj))
    (if (zero? (count obj))
      (to-array-2d [obj])
      (throw (ex-info "Expected a namespace junk" {:else :else})))

    (set? obj)
    (doseq [arg [1 2 3 0]] #(/ 1 arg))

    (or (set? obj) (list? obj))
    (to-array-2d [(vec obj)])

    obj
    (to-array-2d [(object-array obj)])))

(defn any-to-array-2d [obj]
  (cond
    (nil? obj)
    (to-array-2d [])

    (.isArray (class obj))
    (if (zero? (count obj))
      (to-array-2d [obj])
      )
    ))

(map-indexed #(map-indexed + %1 %2) (any-to-array-2d [1 2]))
(nth (any-to-array-2d [1 2]) 0 )
(map-indexed + (nth (any-to-array-2d [1 2 3 7]) 1 ))
(get (any-to-array-2d [1 2 3 7]) 2)
(get (any-to-array-2d [[1 2][3 4][5 6]]) 1)

(defn print-array-2d
  ([arr]
   (with-out-str
     (apply print (print-array-2d arr 0))
     (apply print " / ")
     (apply print (print-array-2d arr 1))
     ))
  ([arr column]
   (map-indexed
    #(pr-str %2)
    (get arr column))))

(print-array-2d
 (object-array [(object-array [1000 10 2 2000 3 "blaa" 0.1])
                (object-array [3000 10 1 4000 1 "braa" 1.1])]) )

(print-array-2d
 (any-to-array-2d nil))
(print-array-2d
 (to-array-2d [[1 2] [1 2]]))
(print-array-2d
 (any-to-array-2d '(10 20)))
(print-array-2d
 (any-to-array-2d {:a 2}))
(print-array-2d
 (any-to-array-2d (to-array-2d [(object-array [1 2])])))

(any-to-array-2d #{1 2})
(any-to-array-2d '(1 2))
(any-to-array-2d {:a 2})
(any-to-array-2d (object-array []))
(any-to-array-2d [])
(any-to-array-2d (to-array-2d [(object-array [1 2])]))
(any-to-array-2d [[-1 -2 -3] 2])

(aget (any-to-array-2d [1 2]) 0 0)
(aget (any-to-array-2d #{1 2}) 0 0)
(aget (any-to-array-2d '(1 2)) 0 0)
(aget (any-to-array-2d {:a 2}) 0 0)
(aget (any-to-array-2d (object-array [1 2])) 0 0)
(aget (any-to-array-2d (to-array-2d [(object-array [1 2])])) 0 0)

(aget (any-to-array-2d [[-1 -2 -3] 2]) 0 0)
