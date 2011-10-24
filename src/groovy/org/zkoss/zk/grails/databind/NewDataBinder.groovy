package org.zkoss.zk.grails.databind

class NewDataBinder {

    def beanMap = [:]
    def subscribeMap = [:]

    //
    //  context
    //  this case should be ViewModel ?
    //
    def bindBean(id, bean) {
        beanMap[id] = bean
    }

    //
    // subscribe to expression
    //
    def addBinding(comp, attr, expr, converter) {
        if(!subscribeMap[expr]) subscribeMap[expr] = []
        subscribeMap[expr] << [comp: comp, attr: attr, converter: converter]
    }

    //
    // firing event from "domain" to "comp"
    // TODO: dealing with sender to prevent SOE
    def fire(sender, expr, value) {
        def list = subscribeMap[expr]
        for (it in list) {
            def attr = it['attr']
            def converter = it['converter']
            def result = converter.coerceToUi(value, it['comp'])
            if(result != IGNORE)
                it['comp']."${attr}" = result
        }
    }

}
