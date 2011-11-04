package org.zkoss.zk.grails.databind

class Observable {

    def object
    private DataBinder binder
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
        if(rootExpr) {
            binder.fireModelChanged(object, rootExpr + "." + name, value)
        } else {
            binder.fireModelChanged(object, name, value)
        }

    }

    @Override
    boolean equals(Object obj) {
        if(obj instanceof Observable)
            return object.equals(obj.object)
        else
            return object.equals(obj)
    }

    @Override
    int hashCode() {
        return object.hashCode()
    }

}
