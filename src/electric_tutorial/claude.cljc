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

(e/defn View [A B C D Z]
  (e/client
    (dom/div
      (dom/props {:class "flex"})
      (dom/div
        (dom/props {:class "w-1/2"})
        (dom/div (A) )
        (dom/div (B))
        (dom/div (C))
        (dom/div (D))
        (Z))
      (dom/div
        (dom/props {:class "w-1/2"})
        (dom/text "abc")
        (MyTextarea (pr-str (Z)))))))
        ;(dom/pre (dom/text (pr-str a b z)))))))

(e/defn Gene []
  (e/client
    (dom/div {:class "container"}
      (let [
            a (e/fn [] (LabeledTextArea "Task Prompt" "a"))
            b (e/fn [] (LabeledTextArea "Task Context" "b" :rows 4))
            c (e/fn [] (LabeledTextArea "Project Context" "c"))
            d (e/fn [] (LabeledTextArea "Project Context" "d" :rows 4))
            z (e/fn []
                (let [e  (dom/div
                           (dom/button
                             (dom/props {:class "bg-gray-500 hover:bg-gray-700 text-white font-bold py-1 px-2 rounded"})
                             (dom/text "Execute")
                             (dom/On "click" identity nil)))
                      [t err] (e/Token e)
                      !c (atom nil)]
                  (when t
                    (case (reset! !c (doto (e/server (Claude (a) (b))) prn))
                      (t)))
                  (e/watch !c)))]
        (View a b c d z)))))
        #_(dom/div
          (dom/props {:class "flex"})
          (dom/div
            (dom/props {:class "w-1/2"})
            (dom/div a)
            (dom/div b)
            (dom/div c)
            (dom/div d))
          (dom/div
            (dom/props {:class "w-1/2"})
            (MyTextarea (pr-str z))
            (dom/pre (dom/text (pr-str a b z)))))