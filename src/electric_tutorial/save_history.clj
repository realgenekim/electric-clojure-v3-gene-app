(ns electric-tutorial.save-history
  (:require
    ;[electric-tutorial.claude :as c]
    [clojure.edn :as edn]
    [electric-tutorial.utils :as u]))

(defn load-history!
  []
  (let [ret (->> (slurp "save-prompt.edn")
              (edn/read-string)
              (remove #(= (-> % :inputs :a) "a"))
              (map-indexed (fn [idx itm]
                             (assoc itm :id idx))))]
    ret))

(defn load-latest-history!
  []
  (->> (load-history!)
    (last)))

(comment
  (load-history!)
  (load-latest-history!)

  (->> (load-latest-history!)
    (spit "chop-book-prompt.edn"))
  0)



(comment
  (do
    @electric-tutorial.claude/!claude-output)
  (count @electric-tutorial.claude/!claude-output)

  ; use this
  (->> @c/!claude-output
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
