(ns eden.ast
  (:require
   [eden.token :as token :refer [identifier?]]
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


(defn consume-token
  [astm chk-fn msg]
  (let [token (current-token astm)]
    (if (chk-fn token)
      (advance-token astm)
      (throw (Throwable. msg)))))


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


(defn parse-statements
  [astm]
  (loop [astm astm
         statements []]
    (if-not (or (check-token astm 'end)
                (check-token astm 'else)
                (check-token astm 'elseif)
                (check-token astm 'until))
      (let [[astm stmt] (call-rule astm ::declaration)]
        (recur astm (conj statements stmt)))
      [astm statements])))


(defn parse-arguments
  "Parses the collection as a set of expressions"
  [oastm]
  (loop [astm (set-tokens oastm (vec (current-token oastm)))
         arg-exprs []]
    (if-not (eot? astm)
      (let [[astm expr] (call-rule astm ::expression)]
        (recur astm (conj arg-exprs expr)))
      [(advance-token oastm) arg-exprs])))


(defn parse [astm tokens]
  (loop [astm (set-tokens astm tokens)
         statements []]
    (if-not (eot? astm)
      (let [[astm stmt] (call-rule astm ::declaration)]
        (recur astm (conj statements stmt)))
      statements)))


(defn declaration-rule
  "Also assignment."
  [astm]
  (cond

    ;; Local Declaration with assignment, local x = value
    (and (check-token astm 'local)
         (check-token (advance-token astm) identifier?)
         (check-token (advance-token (advance-token astm)) '=))
    (let [astm (advance-token astm)
          var (current-token astm)
          astm (advance-token astm)
          [astm expr] (call-rule (advance-token astm) ::expression)]
      [astm (token/->DeclareLocalVariableStatement (:*sm astm) var expr)])

    ;; Local Declaration, local x
    (and (check-token astm 'local)
         (check-token (advance-token astm) identifier?))
    (let [astm (advance-token astm)
          var (current-token astm)]
      [(advance-token astm) (token/->DeclareLocalVariableStatement (:*sm astm) var nil)])

    ;; Declare global, or general assignment
    (and (check-token astm identifier?)
         (check-token (advance-token astm) '=))
    (let [var (current-token astm)
          astm (advance-token astm)
          [astm expr] (call-rule (advance-token astm) ::expression)]
      [astm (token/->DeclareVariableStatement (:*sm astm) var expr)])

    :else (call-rule astm ::statement)))


(defn if-statement-rule
  [astm]
  (let [[astm conditional-expr] (call-rule (advance-token astm) ::expression)
        ;; TODO: expect 'then
        astm (advance-token astm)
        [astm truthy-stmts] (parse-statements astm)]
    (cond
      (check-token astm 'else)
      (let [[astm falsy-stmts] (parse-statements (advance-token astm))]
        ;; TODO: check if it's at the end
        [(advance-token astm)
         (token/->IfConditionalStatement (:*sm astm) conditional-expr truthy-stmts falsy-stmts)])

      ;;(check-token astm 'elseif)
      ;;...

      (check-token astm 'end)
      [(advance-token astm) (token/->IfConditionalStatement
                             (:*sm astm) conditional-expr truthy-stmts [])]

      :else (throw (Throwable. "Failed to find end of if conditional")))))


(defn while-statement-rule
  [astm]
  (let [[astm conditional-expr] (call-rule (advance-token astm) ::expression)
        ;; TODO: expect 'do
        astm (advance-token astm)
        [astm stmts] (parse-statements astm)]
    (cond
      (check-token astm 'end)
      [(advance-token astm) (token/->WhileStatement (:*sm astm) conditional-expr stmts)]
      
      :else (throw (Throwable. "Failed to find end of while conditional")))))


(defn repeat-statement-rule
  [astm]
  (let [[astm stmts] (parse-statements (advance-token astm))]
    (cond
      (check-token astm 'until)
      (let [astm (advance-token astm)
            [astm conditional-expr] (call-rule astm ::expression)]
        [astm (token/->RepeatStatement (:*sm astm) conditional-expr stmts)]))))


(defn for-statement-rule
  [astm]
  (let [astm (consume-token astm #(= % 'for) "Incorrect 'for' Statement Syntax. Expected 'for'")
        iter-var (current-token astm)
        astm (consume-token astm identifier? "Iterator Variable is invalid identifier.")]
    (cond
      (check-token astm '=)
      (let [astm (consume-token astm #(= % '=) "Incorrect 'for' Statement Syntax. Expected '='")
            start-idx (current-token astm)
            astm (consume-token astm int? "Start of for loop must be an integer value.")
            end-idx (current-token astm)
            astm (consume-token astm int? "End of for loop must be an integer value.")
            step (if (check-token astm int?)
                   (current-token astm)
                   1)
            astm (if (check-token astm int?)
                   (consume-token astm int? "Step of for loop must be an integer value.")
                   astm)
            astm (consume-token astm #(= % 'do) "Incorrect 'for' Statement Syntax. Expected 'do'")
            [astm stmts] (parse-statements astm)
            astm (consume-token astm #(= % 'end) "Incorrect 'for' Statement Syntax. Expected 'end'")]
        [astm (token/->ForStatement (:*sm astm) iter-var start-idx end-idx step stmts)])

      (check-token astm 'in)
      (let [astm (consume-token astm #(= % 'in) "Incorrect 'for' Statement Syntax. Expected 'in'")
            [astm coll-expr] (call-rule astm ::expression)
            astm (consume-token astm #(= % 'do) "Incorrect 'for' Statement Syntax. Expected 'do'")
            [astm stmts] (parse-statements astm)
            astm (consume-token astm #(= % 'end) "Incorrect 'for' Statement Syntax. Expected 'end'")]
        [astm (token/->ForEachStatement (:*sm astm) iter-var coll-expr stmts)])

      :else (throw (Throwable. "Incorrect For Statement Syntax")))))


(defn arguments-rule
  [astm]
  (parse-arguments astm))


(defn statement-rule
  [astm]
  (cond
    (check-token astm 'print)
    (let [[astm expr] (call-rule (advance-token astm) ::expression)
          stmt (token/->PrintStatement expr)]
      [astm stmt])

    (check-token astm 'if)
    (call-rule astm ::if-statement)

    (check-token astm 'while)
    (call-rule astm ::while-statement)

    (check-token astm 'repeat)
    (call-rule astm ::repeat-statement)

    (check-token astm 'for)
    (call-rule astm ::for-statement)

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
      (let [[astm expr-right] (call-rule (advance-token astm) ::comparison)
            expr (token/->EqualityExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '!=)
      (let [[astm expr-right] (call-rule (advance-token astm) ::comparison)
            expr (token/->NonEqualityExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn comparison-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::addition)]
    (cond

      (check-token astm '>)
      (let [[astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (token/->GreaterThanExpression expr-left expr-right)]
        (recur [astm expr]))
      
      (check-token astm '>=)
      (let [[astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (token/->GreaterThanOrEqualExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '<)
      (let [[astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (token/->LessThanExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '<=)
      (let [[astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (token/->LessThanOrEqualExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn addition-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::multiplication)]
    (cond

      (check-token astm '+)
      (let [[astm expr-right] (call-rule (advance-token astm) ::multiplication)
            expr (token/->AdditionExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '-)
      (let [[astm expr-right] (call-rule (advance-token astm) ::multiplication)
            expr (token/->SubtractionExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn multiplication-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::unary)]
    (cond

      (check-token astm '*)
      (let [[astm expr-right] (call-rule (advance-token astm) ::unary)
            expr (token/->MultiplicationExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '/)
      (let [[astm expr-right] (call-rule (advance-token astm) ::unary)
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

    :else
    (let [[astm primary-expr] (call-rule astm ::primary)
          _ (println "Primary" primary-expr)
          _ (println "Next Token: " (current-token astm))]
      ;; Determine whether it's a function call, which consists of an expression and an argument list
      ;; <expression> ([arg expressions])
      ;; ie. hello("world")
      (if (check-token astm list?)
        (let [[astm arg-exprs] (call-rule astm ::arguments)]
          [astm (token/->CallFunctionExpression (:*sm astm) primary-expr arg-exprs)])
        [astm primary-expr]))))


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

    (check-token astm identifier?)
    [(advance-token astm) (token/->IdentifierExpression (:*sm astm) (current-token astm))]

    :else (throw (Throwable. (str "Failed to parse expression token: " (current-token astm))))))


(defn astm [*sm]
  (-> (new-astm *sm)
      (add-rule ::declaration declaration-rule)
      (add-rule ::if-statement if-statement-rule)
      (add-rule ::while-statement while-statement-rule)
      (add-rule ::repeat-statement repeat-statement-rule)
      (add-rule ::for-statement for-statement-rule)
      (add-rule ::statement statement-rule)
      (add-rule ::arguments arguments-rule)
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
