(ns electric-tutorial.save-history
  (:require
    [electric-tutorial.claude :as c]
    [clojure.edn :as edn]
    [electric-tutorial.utils :as u]))

(defn load-history!
  []
  (let [s (-> (slurp "save-prompt.edn")
            (edn/read-string))]
    s))

(defn load-latest-history!
  []
  (->> (load-history!)
    (last)))

(comment
  (load-history!)
  (load-latest-history!)
  0)



(comment
  (do
    @c/!claude-output)
  (count @c/!claude-output)

  ; use this
  (->> @c/!claude-output
    (last)
    (#(with-out-str
        (clojure.pprint/pprint %)))
    (spit "save-prompt.edn"))

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
