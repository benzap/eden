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
   (let []
     (with-new-environment *sm
       (doseq [stmt stmts]
         (evaluate-statement stmt)))))

  Expression
  (evaluate-expression [this] this))

  


