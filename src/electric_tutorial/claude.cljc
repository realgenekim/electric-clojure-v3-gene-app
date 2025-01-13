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
    ; this returns a stream of values
    (dom/On "input" (fn [e] (.-value (.-target e))) v)))

(e/defn LabeledTextArea
  [label v & {:keys [rows cols]
              :as args}]
  (dom/div
    (dom/div
      (dom/label (dom/props {:class "font-bold"}) (dom/text label)))
    (MyTextarea v args)))

(e/defn Claude [a b c d]
  (e/server
    (case (e/Task (m/sleep 50))
      {:a a :b b})))

(e/defn View [A B C D Z]
  (e/client
    (dom/div (dom/props {:class "flex"})
      (let [[a b c d] (dom/div (dom/props {:class "w-1/2"})
                        (dom/div [(A) (B) (C) (D)]))]
        ;(+ a b c d)
        a b c d
        (dom/div (dom/props {:class "w-1/2"})
          (let [z (Z a b c d)]
            z

            (dom/text "Claude result")
            (MyTextarea (pr-str z))

            (dom/pre (dom/text (pr-str a b c d)))
            (dom/pre (dom/text (pr-str z)))))))))

(e/defn Gene []
  (e/client
    (dom/div {:class "container"}
      (let [
            A (e/fn [] (LabeledTextArea "Task Prompt" "a"))
            B (e/fn [] (LabeledTextArea "Task Context" "b" :rows 4))
            C (e/fn [] (LabeledTextArea "Project Context" "c"))
            D (e/fn [] (LabeledTextArea "Project Context" "d" :rows 4))
            Z (e/fn [a b c d]
                (let [e  (dom/div
                           (dom/button
                             (dom/props {:class "bg-gray-500 hover:bg-gray-700 text-white font-bold py-1 px-2 rounded"})
                             (dom/text "Execute")
                             (dom/On "click" identity nil)))
                      [t err] (e/Token e)
                      !c (atom nil)]
                  (when t
                    (case (reset! !c (doto (e/server (Claude a b c d)) prn))
                      (t)))
                  (e/watch !c)))]
        (View A B C D Z)))))