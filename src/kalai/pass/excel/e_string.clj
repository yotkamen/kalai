(ns kalai.pass.excel.e-string
  (:require [meander.strategy.epsilon :as s]
            [meander.epsilon :as m]
            [clojure.string :as str]
            [camel-snake-kebab.core :as csk]
            [puget.printer :as puget]
            [clojure.java.io :as io]
            [kalai.pass.java.util :as ju]))

(declare stringify)

;;;; These are helpers

(defn- parens [x]
  (str "(" x ")"))

(defn- comma-separated [& xs]
  (str/join ", " xs))

(defn- semicolumn-separated [& xs]
  (str/join "; " xs))

(defn- params-list [params]
  (parens (apply comma-separated params)))

(defn- args-list [args]
  (parens (apply comma-separated (map stringify args))))

(defn- space-separated [& xs]
  (str/join " " xs))

(defn- line-separated [& xs]
  (str/join \newline xs))

;;;; These are what our symbols should resolve to

;; TODO: do we need an :object type?
(def kalai-type->java
  {:map     "io.lacuna.bifurcan.Map"
   :mmap    "HashMap"
   :set     "io.lacuna.bifurcan.Set"
   :mset    "HashSet"
   :vector  "io.lacuna.bifurcan.List"
   :mvector "ArrayList"
   :bool    "boolean"
   :byte    "byte"
   :char    "char"
   :int     "int"
   :long    "long"
   :float   "float"
   :double  "double"
   :string  "String"
   :void    "void"
   :any     "Object"})

(defn java-type [t]
  (or (get kalai-type->java t)
      ;; TODO: breaking the rules for interop... is this a bad idea?
      (when t (pr-str t))
      "TYPE_MISSING"))

(defn box [s]
  (case s
    "int" "Integer"
    "long" "Long"
    "char" "Character"
    "bool" "Boolean"
    "float" "Float"
    "double" "Double"
    "byte" "Byte"
    "short" "Short"
    s))

(def t-str
  (s/rewrite
    {?t [& ?ts]}
    ~(str (java-type ?t)
          \< (str/join \, (for [t ?ts]
                            (box (t-str t))))
          \>)

    ?t
    ~(str (java-type ?t))))

(defn type-modifiers [s mut global]
  (cond->> s
           (not mut) (space-separated "final")
           global (space-separated "static")))

(defn where [{:keys [file line column]}]
  (when file
    (str (.getName (io/file file)) ":" line ":" column)))

(defn maybe-warn-type-missing [t x]
  (when (str/includes? t "TYPE_MISSING")
    (binding [*print-meta* true]
      (println "WARNING: missing type detected" x
               (where (meta (:expr (meta x))))))))

(defn type-str [variable-name]
  (let [{:keys [t mut global]} (meta variable-name)]
    (-> (t-str t)
        (doto (maybe-warn-type-missing variable-name))
        (type-modifiers mut global))))

(defn lambda-str [params & args]
  (str "LAMBDA"
       (parens (apply semicolumn-separated (map stringify (concat (apply list params) args))))))

(def operators
  {'+ "SUM"
   '- "MINUS"
   '* "MULTIPLY"
   '/ "DIVIDE"
   })

(defn operator-str
  ([op x]
   (str op (stringify x)))
  ([op x & xs]
   (str (get operators op)
        (parens
         (apply semicolumn-separated
                (map stringify (cons x xs)))))))

(defn multiply-str
  ([arg]
   (stringify arg))
  ([arg1 & args]
   (str "MULTIPLY"
        (parens
         (apply semicolumn-separated
                (list (stringify arg1)
                      (apply multiply-str args)))))))

(defn invoke-str [function-name & args]
  (str (stringify function-name)
       (parens (apply semicolumn-separated (map stringify args)))))

(defn map-str [function-name & args]
  (str "MAP"
       (parens
        (str (apply semicolumn-separated (map stringify args)) "; " (stringify function-name)))))

(defn add-str
  ([& args]
   (str "SUM"
        (parens
         (apply semicolumn-separated
                (map stringify args))))))

;;;; This is the main entry point

(def str-fn-map
  {'e/operator                    operator-str
   'e/lambda                      lambda-str
   'e/invoke                      invoke-str
   'e/map                         map-str
   'clojure.lang.Numbers/add      add-str
   'clojure.lang.Numbers/multiply multiply-str
   })

(def stringify
  (s/match
    (?x . !more ... :as ?form)
    (let [f (get str-fn-map ?x)]
      (if f
        (apply f !more)
        (do
          (println "Inner form:")
          (puget/cprint ?form)
          (throw (ex-info (str "Missing function: " ?x)
                          {:form ?form})))))

    (m/pred keyword? ?k)
    (pr-str (str ?k))

    (m/pred char? ?c)
    (str \' ?c \')

    ;; identifier
    (m/pred #(and (symbol? %)
                  (str/includes? (str %) "-"))
            ?s)
    (csk/->camelCase (str ?s))

    ?else
    (pr-str ?else)))

(defn stringify-entry [form]
  (try
    (stringify form)
    (catch Exception ex
      (println "Outer form:")
      (puget/cprint form)
      (throw ex))))
