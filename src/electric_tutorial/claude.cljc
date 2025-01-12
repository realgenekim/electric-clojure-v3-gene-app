(ns electric-tutorial.claude
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [hyperfiddle.electric-forms0 :refer [Input]]
            [missionary.core :as m]))
(e/defn MyTextarea
  [v & {:keys [rows cols]
        :or {rows 10 cols 50}}]
  (dom/textarea
    (dom/props {:rows rows :cols cols :value v})
    (dom/On "input" (fn [e] (.-value (.-target e))) v)))

(e/defn LabeledTextArea
  [label v & {:keys [rows cols]
              :as args}]
  (dom/div
    (dom/div
      (dom/label (dom/props {:class "font-bold"}) (dom/text label)))
    (MyTextarea v args)))

(e/defn Claude [a b]
  (e/server
    (case (e/Task (m/sleep 50))
      {:a a :b b})))

(e/defn Gene []
  (e/client
    (let [
          a (dom/div (LabeledTextArea "Task Prompt" "a"))
          b (dom/div (LabeledTextArea "Task Context" "b" :rows 4))
          c (dom/div (LabeledTextArea "Project Context" "c"))
          d (dom/div (LabeledTextArea "Project Context" "d" :rows 4))
          z (let [e  (dom/div
                       (dom/button
                         (dom/props {:class "bg-gray-500 hover:bg-gray-700 text-white font-bold py-1 px-2 rounded"})
                         (dom/text "Execute")
                         (dom/On "click" identity nil)))
                  [t err] (e/Token e)
                  !c (atom nil)]
              (when t
                (case (reset! !c (doto (e/server (Claude a b)) prn))
                  (t)))
              (e/watch !c))]
      a b c d
      (MyTextarea (pr-str z))
      (dom/pre (dom/text (pr-str a b z))))))