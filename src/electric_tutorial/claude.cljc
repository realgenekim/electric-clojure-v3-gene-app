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
  [label atm & {:keys [rows cols]
                :as args}]
  (let [v (e/watch atm)]
    (dom/div
      (dom/div
        (dom/label (dom/props {:class "font-bold"}) (dom/text label)))
      (MyTextareaAtom v atm args))))

(e/defn Claude [a b c d]
  (e/server
    (case (e/Task (m/sleep 500))
      (let [retval {:a a :b b :c c :d d}]
        retval))))

(declare aria-css)

(e/defn MyButton [F !c]
  " A reactive button component that executes a server-side function F and stores its result in the !c atom.

    F is function to call on server
    !c is the atom to store the result -- initialize to nil
    "
  (dom/button (dom/props {:class (str "bg-gray-500 hover:bg-gray-700 text-white font-bold py-1 px-2 rounded"
                                   aria-css)})
    (dom/text "Execute")
    (let [e (dom/On "click" identity nil)
          [t err] (e/Token e)]
      (dom/props {:aria-busy (some? t) :disabled (some? t) :aria-invalid (some? err)})
      (when t
        (case (reset! !c (e/server (F)))
          (t)))
      (e/watch !c))))

(defonce !a (atom "a"))
(defonce !b (atom "b"))
(defonce !c (atom "c"))
(defonce !d (atom "d"))

(defonce !z (atom nil))

(e/defn Gene []
  (e/client
    (dom/style (dom/text aria-css))
    (dom/div {:class "container mx-auto p-8"}
      (dom/div (dom/props {:class "flex gap-8"})
        (let [[a b c d :as form]
              (dom/div (dom/props {:class "w-1/2 border rounded-lg p-6 shadow-md"})
                (dom/div [(LabeledTextAreaAtom "Task Prompt"  !a)
                          (LabeledTextAreaAtom "Task Context" !b :rows 4)
                          (LabeledTextAreaAtom "Project Context" !c)
                          (LabeledTextAreaAtom "Project Context" !d :rows 4)]))]
          ; this is needed to force evaluation of the elements
          a b c d
          (dom/div (dom/props {:class "w-1/2 border rounded-lg p-6 shadow-md"})
            (let [z (MyButton (e/fn [] (Claude a b c d)) !z)]
              (dom/text "Claude result")
              (MyTextarea (pr-str z)))

            (dom/pre (dom/text (pr-str a b c d)))))))))

(def aria-css
  (str
    "disabled:opacity-50 "
    "aria-[busy=true]:bg-yellow-400 "
    "aria-[invalid=true]:bg-pink-400"))

1
