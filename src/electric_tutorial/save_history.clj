(ns electric-tutorial.save-history
  (:require
    [electric-tutorial.claude :as c]
    [electric-tutorial.utils :as u]))





(comment
  (do
    @c/!claude-output)
  (count @c/!claude-output)

  (spit "save-prompt.edn"
    (with-out-str
      (clojure.pprint/pprint
        (-> @c/!claude-output
            first    ; Get the first item since it's a vector
            (update-in [:inputs :a] #(u/split-and-fmt % 80))
            (update-in [:inputs :b] #(u/split-and-fmt % 80))
            (update-in [:inputs :c] #(u/split-and-fmt % 80))
            (update-in [:inputs :d] #(u/split-and-fmt % 80))
            (update :prompt #(fmt % 80))
            (update :claude-response #(fmt % 80))))))





  ; pretty print
  (spit "save-prompt.edn" (with-out-str (clojure.pprint/pprint @!claude-output)))

  0)
