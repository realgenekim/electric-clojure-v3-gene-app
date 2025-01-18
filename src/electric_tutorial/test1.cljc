(ns electric-tutorial.test1
  (:require
    [hyperfiddle.electric3 :as e]
    [hyperfiddle.electric-dom3 :as dom]))

(e/defn TextConcat []
  (dom/div
    (dom/text "abc")))
