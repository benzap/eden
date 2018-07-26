(ns eden.def
  (:require
   [eden.std.meta :as meta]
   [eden.state-machine :as state]
   [eden.std.expression :refer [Expression evaluate-expression]]))


(defrecord EdenFFI [func]
  meta/EdenCallable
  (__call [_ args]
    (println "args" args)
    (apply func args))

  Expression
  (evaluate-expression [this] this)

  ;; Support calling the eden function from within clojure
  ;; the original interface goes to arg20, so...
  clojure.lang.IFn
  (applyTo [this args]
    (clojure.lang.AFn/applyToHelper this args))
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


(defn wrap-function
  [func]
  (->EdenFFI func))


(defn set-function!
  [eden name func]
  (swap! (:*sm eden) state/set-global-var name (wrap-function func))
  eden)

