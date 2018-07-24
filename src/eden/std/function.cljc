(ns eden.std.function
  (:require
   [eden.std.meta :as meta]
   [eden.std.return :as std.return]
   [eden.state-machine.environment :as environment :refer [with-new-environment]]
   [eden.std.statement :refer [evaluate-statement]]
   [eden.std.expression :refer [Expression evaluate-expression]]
   [eden.state-machine :as state])
  (:import clojure.lang.ExceptionInfo))


(defn add-function-environment
  [*sm *closure]
  ;; Add the closure environment
  (swap! *sm state/add-environment @*closure)
  
  ;; Add the local function environment scope
  (swap! *sm state/add-environment))


(defn remove-function-environment
  [*sm *closure]
  ;; Remove the local function environment scope
  (swap! *sm state/remove-environment)

  ;; Set our closure environment to the updated closure environment
  (let [closure-env (eden.state-machine.environment/get-last-environment *sm)]
    (reset! *closure closure-env))

  ;; Remove the closure environment
  (swap! *sm state/remove-environment))


(defrecord EdenFunction [*sm params stmts *closure]
  meta/EdenCallable
  (__call [_ args]
    (try
      ;; Link the arguments to the parameters
      (let [vparams (vec params)]
        ;; We apply the function's outer scope for the environment
        (add-function-environment *sm *closure)

        ;; All arguments appear in the '... var as a vector
        (swap! *sm state/set-local-var '... (vec args))

        ;; Iterate over each arg, and assign to a parameter
        ;; TODO: check if params consists of identifiers
        ;; TODO: support destructuring
        (doseq [[i arg] (map-indexed vector args)]
          (when-let [param (get vparams i)]
            (swap! *sm state/set-local-var param arg)))
        
        (doseq [stmt stmts]
          (evaluate-statement stmt)))
      
      (catch ExceptionInfo ex
        (std.return/catch-return-value ex))

      (finally
        (remove-function-environment *sm *closure))))

  Expression
  (evaluate-expression [this]
    ;; Assign our closure environment
    (reset! *closure (environment/get-closure-environment *sm))

    this))


;; Include multi-methods for printing, since EdenFunction includes
;; expressions with cyclic atom references.
(defmethod print-method EdenFunction
  [this out]
  (.write out "#EdenFunction{}"))


(defmethod print-dup EdenFunction
  [this out]
  (.write out "#EdenFunction{}"))
