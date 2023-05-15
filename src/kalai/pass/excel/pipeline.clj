(ns kalai.pass.excel.pipeline
  (:require [kalai.pass.excel.a-syntax :as excel.a-syntax]
            [kalai.pass.excel.e-string :as excel.e-string]
            [kalai.pass.shared.flatten-groups :as flatten-groups]
            [kalai.pass.shared.raise-stuff :as raise-stuff]
            [kalai.util :as u]))

(defn kalai->excel [k]
  (->> k
       (flatten-groups/rewrite)
       (excel.a-syntax/rewrite)
       (excel.e-string/stringify-entry)))
