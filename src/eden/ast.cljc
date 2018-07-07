(ns eden.ast)


(defn new-astm []
  {:rules {}
   :tokens []
   :index 0})


(defn add-rule
  [astm head body]
  (update-in astm [:rules] assoc head body))


(defn call-rule
  [astm head]
  (if-let [rule-fn (get (:rules astm) head)]
    (rule-fn astm)
    (throw (Throwable. (str "Failed to find rule function: " head)))))


(defn eot?
  "Returns true if the astm is at the end-of-tokens."
  [{:keys [tokens index]}]
  (= ::eot (nth tokens index)))


(defn current-token
  [{:keys [tokens index] :as astm}]
  (when-not (eot? astm)
    (nth tokens index)))


(defn previous-token
  [{:keys [tokens index]}]
  (if-not (= index 0)
    (nth tokens (dec index))
    (throw (Throwable. "Attempted to retrieve previous token while on the first token."))))


(defn next-token
  [{:keys [tokens index] :as astm}]
  (if-not (eot? (assoc astm :index (inc index)))
    (nth tokens (inc index))
    (throw (Throwable. "Attempted to retrieve the next token while on the last token."))))


(defn advance-token
  [{:keys [index] :as astm}]
  (when-not (eot? astm)
    (assoc astm :index (inc index))))


(defn check-token
  [{:keys [] :as astm} token]
  (if-not (eot? astm)
    (cond
      (fn? token) (token (current-token astm))
      :else (= (current-token astm) token))
    false))


(defn set-tokens
  [astm tokens]
  (assoc astm :tokens (conj (vec tokens) ::eot)))


(defn parse [astm tokens]
  (let [astm (set-tokens astm tokens)
        [_ expr] (call-rule astm ::expression)]
    expr))


(defn expression-rule
  [astm]
  (call-rule astm ::equality))


(defn equality-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::comparison)]
    (if (or (check-token astm '==)
            (check-token astm '!=))
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::comparison)]
        (recur [astm (list operator expr-left expr-right)]))
        [astm expr-left])))


(defn comparison-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::addition)]
    (if (or (check-token astm '>)
            (check-token astm '>=)
            (check-token astm '<)
            (check-token astm '<=))
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::addition)]
        (recur [astm (list operator expr-left expr-right)]))
        [astm expr-left])))


(defn addition-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::multiplication)]
    (if (or (check-token astm '+)
            (check-token astm '-))
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::multiplication)]
        (recur [astm (list operator expr-left expr-right)]))
      [astm expr-left])))


(defn multiplication-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::unary)]
    (if (or (check-token astm '/)
            (check-token astm '*))
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::unary)]
        (recur [astm (list operator expr-left expr-right)]))
        [astm expr-left])))


(defn unary-rule [astm]
  (if (check-token astm '-)
    (let [operator (current-token astm)
          [astm expr-right] (call-rule (advance-token astm) ::unary)]
      (recur [astm (list operator expr-right)]))
    (call-rule astm ::primary)))


(defn primary-rule [astm]
  (cond
    (check-token astm true) [(advance-token astm) true]
    (check-token astm false) [(advance-token astm) false]
    (check-token astm string?) [(advance-token astm) (current-token astm)]
    (check-token astm keyword?) [(advance-token astm) (current-token astm)]
    (check-token astm number?) [(advance-token astm) (current-token astm)]
    (check-token astm nil?) [(advance-token astm) (current-token astm)]
    ;; TODO: groupings ie. ( expression ), [ values ], { key-val }
    :else (throw (Throwable. (str "Failed to parse token: " (current-token astm))))))


;;(comparison-rule (set-tokens astm '[2 + 2 * 4 / 2]))


(def astm
  (-> (new-astm)
      (add-rule ::expression expression-rule)
      (add-rule ::equality equality-rule)
      (add-rule ::comparison comparison-rule)
      (add-rule ::addition addition-rule)
      (add-rule ::multiplication multiplication-rule)
      (add-rule ::unary unary-rule)
      (add-rule ::primary primary-rule)))
      

;;(parse astm '[2 + 2 / 2 > 2 == :key])


(comment
  (defrule expression
    literal
    ::or unary
    ::or binary
    ::or grouping)


  (defrule literal
    number?
    ::or string?
    ::or keyword?
    ::or true
    ::or false
    ::or nil)


  (defrule grouping
    ::list-of expression)


  (defrule unary
    ( - ::or ! ) expression)


  (defrule binary
    expression operator expression)


  (defrule operator
    ==
    ::or !=
    ::or <
    ::or <=
    ::or >
    ::or >=
    ::or +
    ::or -
    ::or *
    ::or /)


  ;; precedence and associativity


  (defrule expression equality)


  (defrule equality
    comparison [( != ::or == ) comparison])


  (defrule comparison
    addition [( > ::or >= ::or < ::or <= ) addition])


  (defrule addition
    multiplication [( - ::or + ) multiplication])


  (defrule multiplication
    unary [( ! ::or - ) unary])


  (defrule unary
    ( ! ::or - ) unary ::or primary)


  (defrule primary
    number?
    ::or string?
    ::or keyword?
    ::or true
    ::or false
    ::or nil
    ::or ::list-of expression)
  
  )
