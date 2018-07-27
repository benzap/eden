(ns eden.def
  (:require
   [eden.std.meta :as meta]
   [eden.state-machine :as state]
   [eden.std.expression :refer [Expression evaluate-expression]]))


(defrecord EdenFFI [func]
  meta/EdenCallable
  (__call [_ args] (apply func args))

  Expression
  (evaluate-expression [this] this)

  ;; Support calling the eden function from within clojure
  ;; the original interface goes to arg20, so...
  #?(:clj clojure.lang.IFn :cljs cljs.core/IFn)
  #?(:clj (applyTo [this args] (clojure.lang.AFn/applyToHelper this args)))
  (#?(:clj invoke :cljs -invoke) [this] (meta/__call this []))
  (#?(:clj invoke :cljs -invoke) [this arg1] (meta/__call this [arg1]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2] (meta/__call this [arg1 arg2]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3] (meta/__call this [arg1 arg2 arg3]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4] (meta/__call this [arg1 arg2 arg3 arg4]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5] (meta/__call this [arg1 arg2 arg3 arg4 arg5]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6] (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8 arg9]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8 arg9 arg10]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8 arg9 arg10 arg11]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8 arg9 arg10 arg11 arg12]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8 arg9 arg10 arg11 arg12 arg13]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8 arg9 arg10 arg11 arg12 arg13
                                  arg14]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8 arg9 arg10 arg11 arg12 arg13
                                  arg14 arg15]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14 arg15]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8 arg9 arg10 arg11 arg12 arg13
                                  arg14 arg15 arg16]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14 arg15
                       arg16]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8 arg9 arg10 arg11 arg12 arg13
                                  arg14 arg15 arg16 arg17]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14 arg15
                       arg16 arg17]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8 arg9 arg10 arg11 arg12 arg13
                                  arg14 arg15 arg16 arg17 arg18]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14 arg15
                       arg16 arg17 arg18]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
                                  arg7 arg8 arg9 arg10 arg11 arg12 arg13
                                  arg14 arg15 arg16 arg17 arg18 arg19]
    (meta/__call this [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8
                       arg9 arg10 arg11 arg12 arg13 arg14 arg15
                       arg16 arg17 arg18 arg19]))
  (#?(:clj invoke :cljs -invoke) [this arg1 arg2 arg3 arg4 arg5 arg6
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

