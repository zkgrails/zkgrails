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
    // TODO: where to get comp, attr, and expr? from binding Map, but which one?
    def propertyMissing(String name) {
        binder.subscribeToExpression(rootExpr + "." + name, comp, attr, expr, ViewModelTypeConverter.instance)
        return object."$name"
    }

    // set
    def propertyMissing(String name, value) {
        object."$name" = value
        binder.fireModelChanged(object, rootExpr + "." + name, value)
    }

}
