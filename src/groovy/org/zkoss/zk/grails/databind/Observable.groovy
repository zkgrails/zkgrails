package org.zkoss.zk.grails.databind

class Observable {

    private obj
    private binder
    private rootExpr

    Observable(obj, binder, rootExpr) {
        this.obj = obj
        this.binder = binder
        this.rootExpr = rootExpr
    }

    def methodMissing(String name, args) {
        return obj."$name"(args)
    }

    def propertyMissing(String name) {
        return obj."$name"
    }

    def propertyMissing(String name, value) {
        obj."$name" = value
        binder.fire(obj, rootExpr + "." + name, value)
    }

}
