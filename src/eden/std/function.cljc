(ns eden.std.function
  (:require
   [eden.std.meta :as meta]
   [eden.std.return :as std.return]
   [eden.state-machine :as state]
   [eden.state-machine.environment :refer [with-new-environment]]
   [eden.std.statement :refer [evaluate-statement]]
   [eden.std.expression :refer [Expression evaluate-expression]]
   [eden.state-machine :as state])
  (:import clojure.lang.ExceptionInfo))




(defrecord EdenFunction [*sm params stmts closure]
  meta/EdenCallable
  (__call [_ args]
    (try
      ;; Link the arguments to the parameters
      (let [vparams (vec params)]
        ;; We apply the function's outer scope for the environment
        (swap! *sm state/add-environment closure)

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
        (swap! *sm state/remove-environment))))

  Expression
  (evaluate-expression [this] this))
