package org.zkoss.zk.grails.databind

import org.zkoss.zkplus.databind.TypeConverter
import org.zkoss.zk.ui.Component

//    data binding is two way invocation
//
//    1. coerceToUi
//      from domain to comp
//      (load)
//      this explains by subscribeToExpression
//
//    and
//    2. coerceToBean
//      from comp to domain
//      (save)
//      this explains by what TODO ?
//

class NewDataBinder {

    private beanMap = [:]
    private subscribeMap = [:]

    //
    //  context
    //  this case should be ViewModel ?
    //
    def bindBean(id, bean) {
        // TODO support collection
        beanMap[id] = new Observable(bean, this, id)
    }

    //
    // subscribe to expression
    //
    def addBinding(Component comp, String attr, String expr, TypeConverter converter) {
        if(!subscribeMap[expr]) subscribeMap[expr] = []
        subscribeMap[expr] << [comp: comp, attr: attr, converter: converter]
    }

    //
    // firing event from "domain" to "comp"
    // TODO: dealing with sender to prevent SOE
    //
    def fireModelChanged(sender, String expr, value) {
        def list = subscribeMap[expr]
        for (it in list) {
            def attr = it['attr']
            def converter = it['converter']
            def result = converter.coerceToUi(value, it['comp'])
            if(result != TypeConverter.IGNORE)
                it['comp']."${attr}" = result
        }
    }

    //
    def fireViewChanged (Component comp, String eventName) {
        def result = converter.coerceToBean(value, comp)
        if(result != TypeConverter.IGNORE) {
            object."expr" = result
        }
    }

    boolean contains (Component comp) {
        return beanMap.containsKey(comp)
    }

}

