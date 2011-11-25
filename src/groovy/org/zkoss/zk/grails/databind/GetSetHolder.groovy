package org.zkoss.zk.grails.databind

class GetSetHolder {

    Closure getter
    Closure setter

    void get(Closure c) {
        getter = c
    }

    void set(Closure c) {
        setter = c
    }

    static getter(Closure c) {
        def g = new GetSetHolder()
        c.delegate = g
        c.resolveStrategy = Closure.DELEGATE_ONLY
        c.call()
        return g.getter
    }

    static setter(Closure c) {
        def g = new GetSetHolder()
        c.delegate = g
        c.resolveStrategy = Closure.DELEGATE_ONLY
        c.call()
        return g.setter
    }

}
