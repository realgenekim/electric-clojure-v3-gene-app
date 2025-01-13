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

(e/defn MyTextareaAtom
  [v atm & {:keys [rows cols]
            :or {rows 10 cols 50}}]
  (dom/textarea
    (dom/props {:rows rows :cols cols :value v})
    ; this returns a stream of values
    (dom/On "input"
      (fn [e]
        (let [v (.-value (.-target e))]
          (reset! atm v)
          v))
      v)))

(e/defn LabeledTextArea
  [label v & {:keys [rows cols]
              :as args}]
  (dom/div
    (dom/div
      (dom/label (dom/props {:class "font-bold"}) (dom/text label)))
    (MyTextarea v args)))

(e/defn LabeledTextAreaAtom
  [label v atm & {:keys [rows cols]
                  :as args}]
  (dom/div
    (dom/div
      (dom/label (dom/props {:class "font-bold"}) (dom/text label)))
    (MyTextareaAtom v atm args)))

(e/defn Claude [a b c d]
  (e/server
    (case (e/Task (m/sleep 500))
      {:a a :b b})))

(e/defn MyButton [F]
  (dom/button (dom/props {:class "bg-gray-500 hover:bg-gray-700 text-white font-bold py-1 px-2 rounded"})
    (dom/text "Execute")
    (let [e (dom/On "click" identity nil)
          [t err] (e/Token e)
          !c (atom nil)]
      (dom/props {:aria-busy (some? t) :disabled (some? t) :aria-invalid (some? err)})
      (when t
        (case (reset! !c (e/server (F)))
          (t)))
      (e/watch !c))))

(declare css)

(defonce !a (atom "a"))

(e/defn Gene []
  (e/client
    (dom/style (dom/text css))
    (dom/div {:class "container"}
      (dom/div (dom/props {:class "flex"})
        (let [[a b c d :as form]
              (dom/div (dom/props {:class "w-1/2"})
                (dom/div [(LabeledTextAreaAtom "Task Prompt" (e/watch !a) !a)
                          (LabeledTextArea "Task Context" "b" :rows 4)
                          (LabeledTextArea "Project Context" "c")
                          (LabeledTextArea "Project Context" "d" :rows 4)]))]
          a b c d
          (dom/div (dom/props {:class "w-1/2"})
            (let [z (MyButton (e/fn [] (Claude a b c d)))]
              (dom/text "Claude result")
              (MyTextarea (pr-str z)))

            (dom/pre (dom/text (pr-str a b c d)))))))))

(def css "
[aria-busy=true] {background-color: yellow;}
[aria-invalid=true] {background-color: pink;}")

1
2