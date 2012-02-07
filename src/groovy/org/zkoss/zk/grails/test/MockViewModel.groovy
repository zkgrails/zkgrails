package org.zkoss.zk.grails.test

import groovy.lang.Closure

class MockViewModel {

    private vm

    MockViewModel(Class vmClass) {
        this.vm = vmClass.newInstance()
    }

    private invokeGetter(Closure c, opt) {
        c.delegate = opt.binder
        c.resolveStrategy = Closure.DELEGATE_FIRST
        return c.call()
    }

    private invokeSetter(Closure c, val, opt) {
        c.delegate = opt.binder
        c.resolveStrategy = Closure.DELEGATE_FIRST
        return c.call(val)
    }

    def propertyMissing(String name) {
        def obj = vm."$name"
        if(obj instanceof Closure) {
            def getter = GetSetHolder.getter(obj)
            return invokeGetter(getter, [binder: new MockBinder(vm: this.vm)])
        }
        return obj
    }

    def propertyMissing(String name, value) {
        def obj = vm."$name"
        if(obj instanceof Closure) {
            def setter = GetSetHolder.setter(obj)
            invokeSetter(setter, value, [binder: new MockBinder(vm: this.vm)])
            return
        }
        vm."$name" = value
    }

}
