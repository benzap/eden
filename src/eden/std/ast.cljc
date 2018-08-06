(ns eden.std.ast
  (:require
   [eden.state-machine.environment :as environment]
   [eden.std.token :as token :refer [identifier?]]
   [eden.std.exceptions :as exceptions :refer [parser-error]]
   [eden.std.expression :as std.expression]
   [eden.std.impl.expression :as expression]
   [eden.std.statement :refer [evaluate-statement isa-statement?]]
   [eden.std.impl.statement :as statement]
   [eden.std.reserved :refer [reserved?]]
   [eden.std.function :as std.function]
   [eden.utils.symbol :as symbol]
   [eden.std.display :refer [display-node]]
   [eden.std.module :as std.module]
   [eden.std.evaluator :refer [read-file]]))


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
    (parser-error astm (str "Failed to find rule function: " head))))


(defn eot?
  "Returns true if the astm is at the end-of-tokens."
  [{:keys [tokens index]}]
  (= ::eot (nth tokens index)))


(defn current-token
  [{:keys [tokens index] :as astm}]
  (when-not (eot? astm)
    (nth tokens index)))


(defn previous-token
  [{:keys [tokens index] :as astm}]
  (if-not (= index 0)
    (nth tokens (dec index))
    (parser-error astm "Attempted to retrieve previous token while on the first token.")))


(defn next-token
  [{:keys [tokens index] :as astm}]
  (if-not (eot? (assoc astm :index (inc index)))
    (nth tokens (inc index))
    (parser-error astm "Attempted to retrieve the next token while on the last token.")))


(defn advance-token
  [{:keys [index] :as astm}]
  (if-not (eot? astm)
    (assoc astm :index (inc index))
    astm))


(defn consume-token
  [astm chk-fn msg]
  (let [chk-fn (if-not (fn? chk-fn) #(= chk-fn %) chk-fn)
        token (current-token astm)]
    (if (chk-fn token)
      (advance-token astm)
      (parser-error astm msg))))


(defn check-token
  [{:keys [] :as astm} token]
  (if-not (eot? astm)
    (cond
      (fn? token) (token (current-token astm))
      :else (= (current-token astm) token))
    false))


(defn set-tokens
  [astm tokens]
  (let [tokens (token/post-process-tokens tokens)]
    (assoc astm
           :tokens (conj (vec tokens) ::eot)
           :index 0)))


(defn parse-expression
  [astm tokens]
  (let [astm (set-tokens astm tokens)
        [_ expr] (call-rule astm ::expression)]
    expr))


(defn parse-expression-list
  "Parses the collection as a set of expressions"
  [astm tokens]
  (loop [astm (set-tokens astm (vec tokens))
         exprs []]
    (if-not (eot? astm)
      (let [[astm expr] (call-rule astm ::expression)]
        (recur astm (conj exprs expr)))
      exprs)))


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


(defn parse-get-property
  [astm expr]
  (let [token (current-token astm)]
    (cond
     (symbol? token)
     (let [keyl (token/dot-assoc->keyword-list token)
           key-expr (parse-expression astm keyl)
           expr (expression/->GetPropertyExpression key-expr expr)]
       [(advance-token astm) expr])

     (vector? token)
     (let [key-expr (parse-expression astm token)
           expr (expression/->GetPropertyExpression key-expr expr)]
       [(advance-token astm) expr]))))


(defn parse [astm tokens]
  (loop [astm (set-tokens astm tokens)
         statements []]
    (if-not (eot? astm)
      (let [[astm stmt] (call-rule astm ::declaration)]
        (recur astm (conj statements stmt)))
      statements)))


(defn parse-assoc-chain [astm]
  (let [var (current-token astm)
        [var & assoc-list] (if (symbol/contains? var '.)
                             (symbol/split var #"\.")
                             [var])]
    (loop [astm (advance-token astm)
           assoc-list (mapv keyword assoc-list)]
      (let [token (current-token astm)]
        (if (token/identifier-assoc? token)
          (cond
            (vector? token)
            (recur (advance-token astm) (concat assoc-list token))
            (symbol/starts-with? token '.)
            (recur (advance-token astm) (concat assoc-list
                                                (token/dot-assoc->keyword-list token))))
          (let [assoc-exprs (parse-expression-list astm assoc-list)]
            [astm [var assoc-exprs]]))))))


(defn declaration-rule
  "Also assignment."
  [astm]
  (cond

    ;; local function declaration
    (and (check-token astm 'local)
         (check-token (advance-token astm) 'function)
         (check-token (advance-token (advance-token astm)) identifier?))
    (let [astm (advance-token astm)
          astm (advance-token astm)
          var (current-token astm)
          astm (consume-token astm identifier? "Function name must be a non-reserved word.")
          params (current-token astm)
          astm (consume-token astm list?
                              (str "Parameters must be provided in the form of a list." params))
          [astm stmts] (parse-statements astm)
          fcn (std.function/->EdenFunction (:*sm astm) params stmts (atom nil))]
      [(advance-token astm) (statement/->DeclareLocalVariableStatement (:*sm astm) var fcn)])

    ;; function declaration
    (and (check-token astm 'function)
         (check-token (advance-token astm) identifier?))
    (let [astm (advance-token astm)
          var (current-token astm)
          astm (consume-token astm identifier? "Function name must be a non-reserved word.")
          params (current-token astm)
          astm (consume-token astm list?
                              (str "Parameters must be provided in the form of a list." params))
          [astm stmts] (parse-statements astm)
          fcn (std.function/->EdenFunction (:*sm astm) params stmts (atom nil))]
      [(advance-token astm) (statement/->DeclareVariableStatement (:*sm astm) var fcn)])

    ;; Local Declaration with assignment, local x = value
    (and (check-token astm 'local)
         (check-token (advance-token astm) identifier?)
         (check-token (advance-token (advance-token astm)) '=))
    (let [astm (advance-token astm)
          var (current-token astm)
          astm (advance-token astm)
          [astm expr] (call-rule (advance-token astm) ::expression)]
      [astm (statement/->DeclareLocalVariableStatement (:*sm astm) var expr)])

    ;; Local Declaration, local x
    (and (check-token astm 'local)
         (check-token (advance-token astm) identifier?))
    (let [astm (advance-token astm)
          var (current-token astm)]
      [(advance-token astm) (statement/->DeclareLocalVariableStatement (:*sm astm) var nil)])

    ;; Declare global, or general assignment
    (and (check-token astm identifier?)
         (check-token (advance-token astm) '=))
    (let [var (current-token astm)
          astm (advance-token astm)
          [astm expr] (call-rule (advance-token astm) ::expression)]
      [astm (statement/->DeclareVariableStatement (:*sm astm) var expr)])

    ;; Lookahead and determine if it's an association chain
    (or
     (and (check-token astm token/identifier?)
          (check-token (advance-token astm) token/identifier-assoc?))
     (check-token astm token/identifier-assoc?))
    (call-rule astm ::associate-chain)

    (check-token astm 'export)
    (let [[astm expr] (call-rule (advance-token astm) ::expression)]
      [astm (statement/->ExportModuleStatement expr)])

    :else (call-rule astm ::statement)))


(defn associate-chain-rule
  [astm]
  (let [[nastm [var assoc-list]] (parse-assoc-chain astm)]
    (if (check-token nastm '=)
       (let [[nastm expr] (call-rule (advance-token nastm) ::expression)]
         [nastm (statement/->AssociateChainStatement (:*sm nastm) var assoc-list expr)])
       (call-rule astm ::statement))))


(defn if-statement-rule
  [astm]
  (loop [astm astm if-stmts [] else-stmt []]
    (cond
      (check-token astm 'if)
      ;; TODO: make sure there's only one if statement
      (let [[astm if-conditional-expr] (call-rule (advance-token astm) ::expression)
            astm (consume-token astm 'then "Missing 'then' within if conditional.")
            [astm truthy-stmts] (parse-statements astm)]
        (recur astm (conj if-stmts [if-conditional-expr truthy-stmts]) else-stmt))

      (check-token astm 'elseif)
      (let [[astm else-if-conditional-expr] (call-rule (advance-token astm) ::expression)
            astm (consume-token astm 'then "Missing 'then' within elseif conditional.")
            [astm truthy-stmts] (parse-statements astm)]
        (recur astm (conj if-stmts [else-if-conditional-expr truthy-stmts]) else-stmt))

      (check-token astm 'else)
      (let [[astm falsy-stmts] (parse-statements (advance-token astm))]
        (recur astm if-stmts falsy-stmts))

      ;; Construct our if-conditional chain
      (check-token astm 'end)
      (let [expr
            (reduce
             (fn [main-stmt [cond-stmt truthy-stmt]]
               (if (nil? main-stmt)
                 (statement/->IfConditionalStatement (:*sm astm) cond-stmt truthy-stmt else-stmt)
                 (statement/->IfConditionalStatement (:*sm astm) cond-stmt truthy-stmt [main-stmt])))
             nil (reverse if-stmts))]
        [(advance-token astm) expr])
      
      :else (parser-error astm "Failed to find end of if conditional"))))


(defn while-statement-rule
  [astm]
  (let [[astm conditional-expr] (call-rule (advance-token astm) ::expression)
        astm (consume-token astm 'do "Expected 'do' token after while conditional.")
        [astm stmts] (parse-statements astm)
        astm (consume-token astm 'end "Expected 'end' token after while statements.")]
    [astm (statement/->WhileStatement (:*sm astm) conditional-expr stmts)]))


(defn repeat-statement-rule
  [astm]
  (let [[astm stmts] (parse-statements (advance-token astm))]
    (cond
      (check-token astm 'until)
      (let [astm (advance-token astm)
            [astm conditional-expr] (call-rule astm ::expression)]
        [astm (statement/->RepeatStatement (:*sm astm) conditional-expr stmts)]))))


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
        [astm (statement/->ForStatement (:*sm astm) iter-var start-idx end-idx step stmts)])

      (check-token astm 'in)
      (let [astm (consume-token astm #(= % 'in) "Incorrect 'for' Statement Syntax. Expected 'in'")
            [astm coll-expr] (call-rule astm ::expression)
            astm (consume-token astm #(= % 'do) "Incorrect 'for' Statement Syntax. Expected 'do'")
            [astm stmts] (parse-statements astm)
            astm (consume-token astm #(= % 'end) "Incorrect 'for' Statement Syntax. Expected 'end'")]
        [astm (statement/->ForEachStatement (:*sm astm) iter-var coll-expr stmts)])

      :else (parser-error astm "Incorrect For Statement Syntax"))))


(defn return-statement-rule
  [astm]
  (let [astm (consume-token astm #(= % 'return) "Return statement must begin with 'return'.")
        [astm expr] (call-rule astm ::expression)]
    [astm (statement/->ReturnStatement expr)]))


(defn arguments-rule
  [astm]
  (parse-arguments astm))


(defn statement-rule
  [astm]
  (cond
    (check-token astm 'if)
    (call-rule astm ::if-statement)

    (check-token astm 'while)
    (call-rule astm ::while-statement)

    (check-token astm 'repeat)
    (call-rule astm ::repeat-statement)

    (check-token astm 'for)
    (call-rule astm ::for-statement)

    (check-token astm 'return)
    (call-rule astm ::return-statement)

    :else
    (let [[astm expr] (call-rule astm ::expression)
          stmt (statement/->ExpressionStatement expr)]
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
            expr (expression/->AndExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm 'or)
      (let [[astm expr-right] (call-rule (advance-token astm) ::equality)
            expr (expression/->OrExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn equality-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::comparison)]
    (cond

      (check-token astm '==)
      (let [[astm expr-right] (call-rule (advance-token astm) ::comparison)
            expr (expression/->EqualityExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '!=)
      (let [[astm expr-right] (call-rule (advance-token astm) ::comparison)
            expr (expression/->NonEqualityExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn comparison-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::addition)]
    (cond

      (check-token astm '>)
      (let [[astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (expression/->GreaterThanExpression expr-left expr-right)]
        (recur [astm expr]))
      
      (check-token astm '>=)
      (let [[astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (expression/->GreaterThanOrEqualExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '<)
      (let [[astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (expression/->LessThanExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '<=)
      (let [[astm expr-right] (call-rule (advance-token astm) ::addition)
            expr (expression/->LessThanOrEqualExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn addition-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::multiplication)]
    (cond

      (check-token astm '+)
      (let [[astm expr-right] (call-rule (advance-token astm) ::multiplication)
            expr (expression/->AdditionExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '-)
      (let [[astm expr-right] (call-rule (advance-token astm) ::multiplication)
            expr (expression/->SubtractionExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn multiplication-rule
  [astm]
  (loop [[astm expr-left] (call-rule astm ::unary)]
    (cond

      (check-token astm '*)
      (let [[astm expr-right] (call-rule (advance-token astm) ::unary)
            expr (expression/->MultiplicationExpression expr-left expr-right)]
        (recur [astm expr]))

      (check-token astm '/)
      (let [[astm expr-right] (call-rule (advance-token astm) ::unary)
            expr (expression/->DivisionExpression expr-left expr-right)]
        (recur [astm expr]))

      :else [astm expr-left])))


(defn unary-rule [astm]
  (cond

    (check-token astm 'not)
    (let [[astm expr-right] (call-rule (advance-token astm) ::primary)
          expr (expression/->NotExpression expr-right)]
      [astm expr])

    (check-token astm '-)
    (let [[astm expr-right] (call-rule (advance-token astm) ::primary)
          expr (expression/->NegationExpression expr-right)]
      [astm expr])

    (check-token astm 'require)
    (let [astm (advance-token astm)
          smodule (current-token astm)
          astm (consume-token astm string? "Module name must be a string")
          spath (std.module/resolve-module-file-path smodule)]
      (cond
        (string? spath)
        (let [tokens (read-file spath)
              stmts (parse astm tokens)]
          
          [astm (statement/->RequireModuleStatement (:*sm astm) spath stmts (atom nil))])

        (nil? spath)
        (parser-error astm (str "Given module '" smodule "' could not be found"))

        :else
        (parser-error astm "Given module path needs to be a string.")))

    :else
    (let [[astm primary-expr] (call-rule astm ::anonymous-function)]

      ;; Determine if the expression has a chain of function calls, accessors, etc.
      (loop [astm astm
             primary-expr primary-expr]

       (cond
         (check-token astm list?)
         (let [[astm arg-exprs] (call-rule astm ::arguments)]
           (recur astm (expression/->CallFunctionExpression (:*sm astm) primary-expr arg-exprs)))
         
         (and (check-token astm token/identifier-assoc?))
         (let [[astm primary-expr] (parse-get-property astm primary-expr)]
           (recur astm primary-expr))

         :else [astm primary-expr])))))


(defn anonymous-function-rule
  [astm]
  (cond

    (check-token astm 'function)
    (let [astm (advance-token astm)
          params (current-token astm)
          astm (consume-token astm list? "Parameters must be provided in the form of a list.")
          [astm stmts] (parse-statements astm)
          fcn (std.function/->EdenFunction (:*sm astm) params stmts (atom nil))]
      [(advance-token astm) fcn])

    :else
    (call-rule astm ::primary)))


(defn primary-rule [astm]
  (cond
    (check-token astm true)
    [(advance-token astm) (expression/->BooleanExpression true)]

    (check-token astm false)
    [(advance-token astm) (expression/->BooleanExpression false)]

    (check-token astm string?)
    [(advance-token astm) (expression/->StringExpression (current-token astm))]

    (check-token astm keyword?)
    [(advance-token astm) (expression/->KeywordExpression (current-token astm))]

    (check-token astm float?)
    [(advance-token astm) (expression/->FloatExpression (current-token astm))]

    (check-token astm integer?)
    [(advance-token astm) (expression/->IntegerExpression (current-token astm))]

    (check-token astm nil?)
    [(advance-token astm) (expression/->NullExpression)]

    (check-token astm list?)
    [(advance-token astm) (parse-expression astm (current-token astm))]

    (check-token astm vector?)
    [(advance-token astm)
     (expression/->VectorExpression
      (parse-expression-list astm (current-token astm)))]

    (check-token astm map?)
    [(advance-token astm)
     (expression/->HashMapExpression
      (into {} (for [[k v] (current-token astm)]
                 [(parse-expression astm [k]) (parse-expression astm [v])])))]

    (check-token astm set?)
    [(advance-token astm)
     (expression/->HashSetExpression
      (set (for [val (current-token astm)]
             (parse-expression astm [val]))))]

    (check-token astm identifier?)
    [(advance-token astm) (expression/->IdentifierExpression (:*sm astm) (current-token astm))]

    :else (parser-error astm (str "Failed to parse expression token: " (current-token astm)))))


(defn astm
  [*sm]
  (-> (new-astm *sm)
      (add-rule ::declaration declaration-rule)
      (add-rule ::associate-chain associate-chain-rule)
      (add-rule ::if-statement if-statement-rule)
      (add-rule ::while-statement while-statement-rule)
      (add-rule ::repeat-statement repeat-statement-rule)
      (add-rule ::for-statement for-statement-rule)
      (add-rule ::return-statement return-statement-rule)
      (add-rule ::statement statement-rule)
      (add-rule ::anonymous-function anonymous-function-rule)
      (add-rule ::arguments arguments-rule)
      (add-rule ::expression expression-rule)
      (add-rule ::logical logical-rule)
      (add-rule ::equality equality-rule)
      (add-rule ::comparison comparison-rule)
      (add-rule ::addition addition-rule)
      (add-rule ::multiplication multiplication-rule)
      (add-rule ::unary unary-rule)
      (add-rule ::primary primary-rule)))


(defn evaluate-expression
  "Evaluate the tokens as an eden expression, and return the result."
  [astm tokens]
  (let [syntax-tree (parse-expression astm tokens)]
    (std.expression/evaluate-expression syntax-tree)))


(defn evaluate
  "Evaluate the tokens in the provided eden state machine."
  [astm tokens]
  (let [statements (parse astm tokens)]
    (doseq [stmt statements]
      (evaluate-statement stmt))))
