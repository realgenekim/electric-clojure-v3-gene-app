(ns electric-tutorial.claude
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [hyperfiddle.electric-forms0 :refer [Input]]
            [missionary.core :as m]))

(e/defn MyTextarea [v]
  (dom/textarea
    (dom/props {:rows 10 :cols 50 :value v})
    (dom/On "input" (fn [e] (.-value (.-target e))) v)))

(e/defn Claude [a b]
  (e/server
    (case (e/Task (m/sleep 50))
      {:a a :b b})))

(e/defn Gene []
  (e/client
    (let [
          a (dom/div (MyTextarea "a"))
          b (dom/div (MyTextarea "b"))
          c (let [e (dom/button (dom/text "click")
                      (dom/On "click" identity nil))
                  [t err] (e/Token e)
                  !c (atom nil)]
              (when t
                (case (reset! !c (doto (e/server (Claude a b)) prn))
                  (t)))
              (e/watch !c))]
      a b
      (MyTextarea (pr-str c))
      (dom/pre (dom/text (pr-str a b c))))))