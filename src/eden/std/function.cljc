(ns eden.std.function
  (:require
   [eden.std.meta :as meta]
   [eden.state-machine.environment :refer [with-new-environment]]
   [eden.std.statement :refer [evaluate-statement]]
   [eden.std.expression :refer [Expression evaluate-expression]]
   [eden.state-machine :as state]))


(defrecord EdenFunction [*sm params stmts]
  meta/EdenCallable
  (__call [_ args]
   ;; Link the arguments to the parameters
   (let [vparams (vec params)]
     (with-new-environment *sm
       ;; All arguments appear in the '... var as a vector
       (swap! *sm state/set-local-var '... (vec args))

       ;; Iterate over each arg, and assign to a parameter
       ;; TODO: check if params consists of identifiers
       ;; TODO: support destructuring
       (doseq [[i arg] (map-indexed vector args)]
         (when-let [param (get vparams i)]
           (swap! *sm state/set-local-var param arg)))
       
       (doseq [stmt stmts]
         (evaluate-statement stmt)))))

  Expression
  (evaluate-expression [this] this))
