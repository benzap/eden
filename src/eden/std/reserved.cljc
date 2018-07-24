(ns eden.std.reserved)


(def ^:dynamic *reserved-words*
  '[
    = ..= .=
    and or
    == !=
    > >= < <=
    + -
    * /
    not -

    end
    if then else elseif
    while do
    until repeat
    function ->
    ])


(defn reserved? [sym]
  (contains? (set *reserved-words*) sym))
