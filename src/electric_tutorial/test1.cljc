(ns electric-tutorial.test1
  (:require
    [hyperfiddle.electric3 :as e]
    [hyperfiddle.electric-dom3 :as dom]))

#_(e/defn TextConcat []
    (dom/div
      (dom/text "abc")))

(defonce !text1 (atom "t1"))
(defonce !text2 (atom "t2"))
(defonce !result (atom "t3"))


(e/defn TextConcat []
  (let [
        ;!text1  (atom "")
        ;!text2  (atom "")
        ;!result (atom "")
        text1   (e/watch !text1)
        text2   (e/watch !text2)
        result  (e/watch !result)]

    (dom/div (dom/props {:style {:display "flex"
                                 :gap     "1rem"
                                 :padding "1rem"}})
      ;; Left column
      (dom/div (dom/props {:style {:width          "50%"
                                   :display        "flex"
                                   :flex-direction "column"
                                   :gap            "1rem"}})
        (dom/textarea
          (dom/props {:style {:height        "10rem"
                              :padding       "0.5rem"
                              :border        "1px solid #ccc"
                              :border-radius "4px"}
                      :value text1})


          (dom/On "input" (fn [e] (reset! !text1 (.. e -target -value)))
            nil))

        (dom/textarea
          (dom/props {:style {:height        "10rem"
                              :padding       "0.5rem"
                              :border        "1px solid #ccc"
                              :border-radius "4px"}
                      :value text2})
          (dom/On "input" (fn [e] (reset! !text2 (.. e -target -value)))
            nil))

        (dom/button
          (dom/props {:style {:background-color "#3b82f6"
                              :color            "white"
                              :padding          "0.5rem 1rem"
                              :border-radius    "4px"
                              :cursor           "pointer"}})
          (dom/text "Execute")
          (dom/On "click" (fn [_] (reset! !result (str text1 text2)))
            nil)))


      ;; Right column
      (dom/div (dom/props {:style {:width "50%"}})
        (dom/textarea
          (dom/props {:style    {:width         "100%"
                                 :height        "100%"
                                 :padding       "0.5rem"
                                 :border        "1px solid #ccc"
                                 :border-radius "4px"}
                      :readOnly true})
          (dom/text result))))

    (dom/div
      (dom/text
        (pr-str {:text1 text1 :text2 text2 :result result})))))