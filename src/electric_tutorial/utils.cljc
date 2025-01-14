(ns electric-tutorial.utils
  (:require
    [clojure.string :as str]))

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

(comment
  ;; Example usage:
  (fmt "Here is a very long string that needs to be wrapped at 80 columns" 20)
  ;; => ["Here is a very long" "string that needs to" "be wrapped at 80" "columns"]
  ;;)
  0)

(defn split-on-newlines
  [s]
  (clojure.string/split s #"\n"))

(defn split-and-fmt [s width]
  (->> s
    split-on-newlines
    (map #(fmt % width))
    (mapcat identity)))


(comment

  (split-on-newlines "abc\ndef")

  (->> "Here is a very long string that needs to be wrapped at 80 columns"
    split-on-newlines
    (map #(fmt % 20)))

  (->> "Here is\n a very long string that needs to be wrapped at 80 columns"
    split-on-newlines
    (map #(fmt % 20))
    (mapcat identity))

  (split-and-fmt "Here is\n a very long string that needs to be wrapped at 80 columns" 20)
  0)

(defn split-and-fmt-single-string [s width]
  (->> s
    (split-on-newlines)
    (map #(fmt % width))
    (mapcat identity)
    (str/join "\n")))

(comment
  (split-and-fmt-single-string "Here is\n a very long string that needs to be wrapped at 80 columns" 20)

  0)

