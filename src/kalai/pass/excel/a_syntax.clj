(ns kalai.pass.excel.a-syntax
  (:require [kalai.util :as u]
            [meander.strategy.epsilon :as s]
            [meander.epsilon :as m]))

;; half Clojure half Excel
(def expression
  (s/rewrite
    ;; operator usage
    (operator ?op . !args ...)
    (e/operator ?op . (m/app expression !args) ...)

    ;; function invocation
    (m/and (invoke fn* (invoke [. !param ...] !forms))
           (m/app meta ?meta))
    (m/app with-meta
           (e/lambda [. !param ...] . (m/app expression !forms) ...)
           ?meta)

    (m/and (invoke map ?f . !cols ...)
           (m/app meta ?meta))
    (m/app with-meta
           (e/map (m/app expression ?f) . (m/app expression !cols) ...)
           ?meta)

    ;; function invocation
    (m/and (invoke ?f . !args ...)
           (m/app meta ?meta))
    (m/app with-meta
           (e/invoke (m/app expression ?f) . (m/app expression !args) ...)
           ?meta)

    ?else
    ?else))

(def rewrite
  (s/rewrite
   (namespace ?ns-name
              (function !fn [] ?forms))
   (m/app expression ?forms)

   ?else ~(throw (ex-info "Expected a namespace" {:else ?else}))))
