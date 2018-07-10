(ns eden.ast
  (:require
   [eden.token :as token]
   [eden.reserved :refer [reserved?]]))


(defn new-astm [*sm]
  {:rules {}
   :tokens []
   :index 0
   :*sm *sm})


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
  (if-not (eot? astm)
    (assoc astm :index (inc index))
    astm))


(defn check-token
  [{:keys [] :as astm} token]
  (if-not (eot? astm)
    (cond
      (fn? token) (token (current-token astm))
      :else (= (current-token astm) token))
    false))


(defn set-tokens
  [astm tokens]
  (assoc astm
         :tokens (conj (vec tokens) ::eot)
         :index 0))


(defn parse-expression
  [astm tokens]
  (let [astm (set-tokens astm tokens)
        [_ expr] (call-rule astm ::expression)]
    expr))


(defn parse [astm tokens]
  (loop [astm (set-tokens astm tokens)
         statements []]
    (if-not (eot? astm)
      (let [[astm stmt] (call-rule astm ::statement)]
        (recur astm (conj statements stmt)))
      statements)))


(defn statement-rule
  [astm]
  (cond
    (check-token astm 'print)
    (let [[astm expr] (call-rule (advance-token astm) ::expression)
          stmt (token/->PrintStatement expr)]
      [astm stmt])

    :else
    (let [[astm expr] (call-rule astm ::expression)
          stmt (token/->ExpressionStatement expr)]
      [astm stmt])))


(defn expression-rule
  [astm]
  (call-rule astm ::logical))


(defn logical-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::equality)]
    (cond
      (check-token astm 'and)
      (let [[astm expr-right] (call-rule (advance-token astm) ::equality)
            expr (token/->AndExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm 'or)
      (let [[astm expr-right] (call-rule (advance-token astm) ::equality)
            expr (token/->OrExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn equality-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::comparison)]
    (cond

      (check-token astm '==)
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::comparison)
            expr (token/->EqualityExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '!=)
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::comparison)
            expr (token/->NonEqualityExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn comparison-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::addition)]
    (cond

      (check-token astm '>)
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (token/->GreaterThanExpression expr-left expr-right)]
        (recur [astm expr]))
      
      (check-token astm '>=)
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (token/->GreaterThanOrEqualExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '<)
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (token/->LessThanExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '<=)
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (token/->LessThanOrEqualExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn addition-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::multiplication)]
    (cond

      (check-token astm '+)
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::multiplication)
            expr (token/->AdditionExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '-)
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::multiplication)
            expr (token/->SubtractionExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn multiplication-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::unary)]
    (cond

      (check-token astm '*)
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::unary)
            expr (token/->MultiplicationExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '/)
      (let [operator (current-token astm)
            [astm expr-right] (call-rule (advance-token astm) ::unary)
            expr (token/->DivisionExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn unary-rule [astm]
  (cond

    (check-token astm 'not)
    (let [[astm expr-right] (call-rule (advance-token astm) ::primary)
          expr (token/->NotExpression expr-right)]
      [astm expr])

    (check-token astm '-)
    (let [[astm expr-right] (call-rule (advance-token astm) ::primary)
          expr (token/->NegationExpression expr-right)]
      [astm expr])

    :else (call-rule astm ::primary)))


(defn identifier? [sym]
  (and
   (symbol? sym)
   (not (reserved? sym))))


(defn primary-rule [astm]
  (cond
    (check-token astm true)
    [(advance-token astm) (token/->BooleanExpression true)]

    (check-token astm false)
    [(advance-token astm) (token/->BooleanExpression false)]

    (check-token astm string?)
    [(advance-token astm) (token/->StringExpression (current-token astm))]

    (check-token astm keyword?)
    [(advance-token astm) (token/->KeywordExpression (current-token astm))]

    (check-token astm float?)
    [(advance-token astm) (token/->FloatExpression (current-token astm))]

    (check-token astm integer?)
    [(advance-token astm) (token/->IntegerExpression (current-token astm))]

    (check-token astm nil?)
    [(advance-token astm) (token/->NullExpression)]

    (check-token astm list?)
    [(advance-token astm) (parse-expression astm (current-token astm))]

    (check-token astm vector?)
    [(advance-token astm)
     (token/->VectorExpression
      (vec (for [val (current-token astm)]
             (parse-expression astm [val]))))]

    (check-token astm map?)
    [(advance-token astm)
     (token/->HashMapExpression
      (into {} (for [[k v] (current-token astm)]
                 [(parse-expression astm [k]) (parse-expression astm [v])])))]

    (check-token astm set?)
    [(advance-token astm)
     (token/->HashSetExpression
      (set (for [val (current-token astm)]
             (parse-expression astm [val]))))]

    ;; TODO: exclude reserved
    (check-token astm identifier?)
    [(advance-token astm) (token/->IdentifierExpression (:*sm astm) (current-token astm))]

    :else (throw (Throwable. (str "Failed to parse expression token: " (current-token astm))))))


(defn astm [*sm]
  (-> (new-astm *sm)
      (add-rule ::statement statement-rule)
      (add-rule ::expression expression-rule)
      (add-rule ::logical logical-rule)
      (add-rule ::equality equality-rule)
      (add-rule ::comparison comparison-rule)
      (add-rule ::addition addition-rule)
      (add-rule ::multiplication multiplication-rule)
      (add-rule ::unary unary-rule)
      (add-rule ::primary primary-rule)))


(defn evaluate-expression [astm tokens]
  (let [syntax-tree (parse-expression astm tokens)]
    (token/evaluate-expression syntax-tree)))


(defn evaluate [astm tokens]
  (let [statements (parse astm tokens)]
    (doseq [stmt statements]
      (token/evaluate-statement stmt))))


(comment

  ;; precedence and associativity

  (defrule program [statement] ::eof)

  (defrule statement expression ::or print)

  (defrule expression equality)


  (defrule equality
    comparison [( != ::or == ) comparison])


  (defrule comparison
    addition [( > ::or >= ::or < ::or <= ) addition])


  (defrule addition
    multiplication [( - ::or + ) multiplication])


  (defrule multiplication
    unary [( not ::or - ) unary])


  (defrule unary
    ( ! ::or - ) unary ::or primary)


  (defrule primary
    number?
    ::or string?
    ::or keyword?
    ::or true
    ::or false
    ::or nil
    ::or ::list-of expression))
  
  
