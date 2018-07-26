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

    this)

  ;; Support calling the eden function from within clojure
  ;; the original interface goes to arg20, so...
  clojure.lang.IFn
  (applyTo [this args] (clojure.lang.AFn/applyToHelper this args))
  (invoke [this] (meta/__call this []))
  (invoke [this arg1] (meta/__call this [arg1]))
  (invoke [this arg1 arg2] (meta/__call this [arg1 arg2]))
  (invoke [this arg1 arg2 arg3] (meta/__call this [arg1 arg2 arg3]))
  (invoke [this arg1 arg2 arg3 arg4] (meta/__call this [arg1 arg2 arg3 arg4]))
  (invoke [this arg1 arg2 arg3 arg4 arg5] (meta/__call this [arg1 arg2 arg3 arg4 arg5]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6] (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9 arg10]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9 arg10 arg11]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9 arg10 arg11 arg12]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9 arg10 arg11 arg12 arg13]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9 arg10 arg11 arg12 arg13
           arg14]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9 arg10 arg11 arg12 arg13
           arg14 arg15]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14 arg15]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9 arg10 arg11 arg12 arg13
           arg14 arg15 arg16]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14 arg15
                       arg16]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9 arg10 arg11 arg12 arg13
           arg14 arg15 arg16 arg17]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14 arg15
                       arg16 arg17]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9 arg10 arg11 arg12 arg13
           arg14 arg15 arg16 arg17 arg18]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14 arg15
                       arg16 arg17 arg18]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9 arg10 arg11 arg12 arg13
           arg14 arg15 arg16 arg17 arg18 arg19]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14 arg15
                       arg16 arg17 arg18 arg19]))
  (invoke [this arg1 arg2 arg3 arg4 arg5 arg6
           arg7 arg8 arg9 arg10 arg11 arg12 arg13
           arg14 arg15 arg16 arg17 arg18 arg19 arg20]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14 arg15
                       arg16 arg17 arg18 arg19 arg20])))

  


;; Include multi-methods for printing, since EdenFunction includes
;; expressions with cyclic atom references.
(defmethod print-method EdenFunction
  [this out]
  (.write out "#EdenFunction{}"))


(defmethod print-dup EdenFunction
  [this out]
  (.write out "#EdenFunction{}"))
