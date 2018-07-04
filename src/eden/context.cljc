(ns eden.context)


(defprotocol EdenContext
    [scope-listing
     code-queue])
