(ns genek.claude
  (:require
    [claude.main :as cm]))

(defn call-claude
  [s]
  (cm/call-claude s))

(comment
  (cm/call-claude "write a 20 word poem on Clojure")
  0)
