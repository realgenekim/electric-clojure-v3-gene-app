(ns electric-tutorial.save-history
  (:require
    [electric-tutorial.claude :as c]))

(defn fmt [s width]
  (some-> s
          str
          ;(clojure.string/split #"\n")
          (clojure.string/split #"\s+")
          (->> (reduce (fn [[lines curr-line] word]
                        (if (> (+ (count curr-line) (count word) 1) width)
                          [(conj lines curr-line) word]
                          [lines (str curr-line (when (seq curr-line) " ") word)]))
                      [[] ""]))
          ((fn [[lines last-line]]
             (if (seq last-line)
               (conj lines last-line)
               lines)))))

(defn split-on-newlines
  [s]
  (clojure.string/split s #"\n"))

(defn split-and-fmt [s width]
  (->> s
    split-on-newlines
    (map #(fmt % width))
    (mapcat identity)))

(comment
  ;; Example usage:
  (fmt "Here is a very long string that needs to be wrapped at 80 columns" 20)
  (split-on-newlines "abc\ndef")
  ;; => ["Here is a very long" "string that needs to" "be wrapped at 80" "columns"]
  ;;
  (->> "Here is a very long string that needs to be wrapped at 80 columns"
    split-on-newlines
    (map #(fmt % 20)))

  (->> "Here is\n a very long string that needs to be wrapped at 80 columns"
    split-on-newlines
    (map #(fmt % 20))
    (mapcat identity))

  (split-and-fmt "Here is\n a very long string that needs to be wrapped at 80 columns" 20)
  0)


(comment
  (do
    @c/!claude-output)
  (count @c/!claude-output)

  (spit "save-prompt.edn"
    (with-out-str
      (clojure.pprint/pprint
        (-> @c/!claude-output
            first    ; Get the first item since it's a vector
            (update-in [:inputs :a] #(split-and-fmt % 80))
            (update-in [:inputs :b] #(split-and-fmt % 80))
            (update-in [:inputs :c] #(split-and-fmt % 80))
            (update-in [:inputs :d] #(split-and-fmt % 80))
            (update :prompt #(fmt % 80))
            (update :claude-response #(fmt % 80))))))





  ; pretty print
  (spit "save-prompt.edn" (with-out-str (clojure.pprint/pprint @!claude-output)))

  0)
