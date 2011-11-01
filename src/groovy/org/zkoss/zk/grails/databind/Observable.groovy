package org.zkoss.zk.grails.databind

class Observable {

    def object
    private NewDataBinder binder
    private rootExpr

    Observable(object, binder, rootExpr) {
        this.object = object
        this.binder = binder
        this.rootExpr = rootExpr
    }

    def methodMissing(String name, args) {
        return object."$name"(args)
    }

    // get
    def propertyMissing(String name) {
        return object."$name"
    }

    // set
    def propertyMissing(String name, value) {
        object."$name" = value
        binder.fireModelChanged(object, rootExpr + "." + name, value)
    }

}
