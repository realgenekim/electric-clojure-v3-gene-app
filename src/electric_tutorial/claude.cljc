(ns electric-tutorial.claude
  (:require
    [electric-tutorial.utils :as u]
    #?(:clj [genek.claude :as gclaude])
    [hyperfiddle.electric3 :as e]
    [hyperfiddle.electric-dom3 :as dom]
    [hyperfiddle.electric-forms0 :refer [Input]]
    [missionary.core :as m]))

(e/defn MyTextarea
  [v & {:keys [rows cols]
        :or {rows 10 cols 50}}]
  (let [newv (if (string? v) v (str v))]
    (dom/textarea
      (dom/props {:rows rows :cols cols :value newv
                  :class "w-full"})
      ; this returns a stream of values
      (dom/On "input" (fn [e] (.-value (.-target e))) v))))

(e/defn MyTextareaAtom
  [v atm & {:keys [rows cols]
            :or {rows 10 cols 50}}]
  (dom/textarea
    (dom/props {:rows rows :cols cols :value v
                :class "w-full"})
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

(defonce !claude-output (atom []))

(defn create-prompt [a b c d]
  (let [prompt (str
                 "Task Prompt: " a "\n\n"
                 "Task Context: " b "\n\n"
                 "Project Prompt: " c "\n\n"
                 "Project Context: " d "\n\n")
        inputs {:a a :b b :c c :d d}]
    {:prompt prompt
     :inputs inputs}))


(e/defn Claude [a b c d]
  (e/server
    ;(case (e/Task (m/sleep 500)))
    (let [prompt-data (create-prompt a b c d)
          retval (gclaude/call-claude (:prompt prompt-data))
          save (assoc prompt-data :claude-response retval)]
      (def RETVAL retval)
      (def SAVEPROMPT save)
      (swap! !claude-output conj save)
      (e/client
        (reset! !claude-output save))
      retval)))

(comment
  @!claude-output
  (count @!claude-output)

  ; pretty print
  (spit "save-prompt.edn" (with-out-str (clojure.pprint/pprint @!claude-output)))

  0)

(e/defn CopyEntireConversation [a b c d z])

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

(defonce !summary (atom nil))

(e/defn split-and-fmt-single-string [s width]
  ;(u/split-and-fmt-single-string s width)
  (u/split-on-newlines s))
  ;"abcdef")

(e/defn Gene []
  (e/client
    (dom/style (dom/text aria-css))
    (dom/div {:class "container mx-auto p-8"}
      (dom/div (dom/props {:class "flex gap-2"})
        (let [[a b c d :as form]
              (dom/div (dom/props {:class "w-1/2 border rounded-lg p-2 shadow-md"})
                (dom/div [(LabeledTextAreaAtom "Task Prompt"  !a)
                          (LabeledTextAreaAtom "Task Context" !b :rows 4)
                          (LabeledTextAreaAtom "Project Context" !c)
                          (LabeledTextAreaAtom "Project Context" !d :rows 4)]))]
          ; this is needed to force evaluation of the elements
          a b c d
          (dom/div (dom/props {:class "w-1/2 border rounded-lg p-2 shadow-md"})
            (let [z (MyButton (e/fn [] (Claude a b c d)) !z)]
              ;(reset! !summary (pr-str a b c d))
              (reset! !summary (pr-str a b c d))
              (dom/div
                (MyTextarea z :rows 30)))

            ; TODO: for Dustin
            ; I have to copy this into a global atom, to render it outside of the div
            (dom/pre (dom/text (pr-str a b c d)))
            (println a))))

      (dom/div (dom/props {:class "p-8"})
        ;(let [vsum (e/watch !summary)
        (let [vsum (e/watch !claude-output)
              ;v "line 1\nline 2"]
              v vsum
              vamb (u/split-on-newlines (-> v :claude-response))]
          (e/for [x (e/diff-by identity vamb)]
            (dom/p
              (dom/props {:class "mb-4"})
              (dom/text x))))))))

1


(comment
  ; this works
  ; (e/for [x (e/amb 1 2 3)]
  ;          (dom/p (dom/text (str x))))

  (u/split-and-fmt-single-string "abc\ndef" 80)
  (->> [1 2 3]
    (map (e/fn [x]
           (dom/p (dom/text (str x))))))
  (dom/ul
    (e/for [x [1 2 3 4]]
      (dom/li (dom/text (str x)))))
  ;(dom/p (dom/text "Summary"))
  ;(dom/p (dom/text "Summary"))
  (->> "abc\ndef"
    (split-and-fmt-single-string 80)
    (map (e/fn [x]
           (dom/p (dom/text x))))))

(def aria-css
  (str
    "disabled:opacity-50 "
    "aria-[busy=true]:bg-yellow-400 "
    "aria-[invalid=true]:bg-pink-400"))

1
