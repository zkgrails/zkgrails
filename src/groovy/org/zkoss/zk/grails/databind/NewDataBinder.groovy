package org.zkoss.zk.grails.databind

import org.zkoss.zkplus.databind.TypeConverter
import org.zkoss.zk.ui.Component
import java.lang.reflect.Field

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
    private exprSubscribeMap = [:]
    private compSubscribeMap = [:]

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
        if(!exprSubscribeMap[expr]) exprSubscribeMap[expr] = []
        if(!compSubscribeMap[comp]) compSubscribeMap[comp] = []
        def entry = [comp: comp, attr: attr, expr: expr, converter: converter]
        exprSubscribeMap[expr] << entry
        compSubscribeMap[comp] << entry
    }

    //
    // firing event from "domain" to "comp"
    // TODO: dealing with sender to prevent SOE
    //
    def fireModelChanged(sender, String expr, value) {
        def list = exprSubscribeMap[expr]
        for (it in list) {
            def attr = it['attr']
            def converter = it['converter']
            def result = converter.coerceToUi(value, it['comp'])
            if(result != TypeConverter.IGNORE)
                it['comp']."${attr}" = result
        }
    }

    // TODO: unit test
    def eval(bean, String[] expr) {
        if (expr.size() == 1) {
            def value = bean
            if(value instanceof Observable) value = value.object
            return value
        } else {
            def value = bean
            for(ex in expr) {
                if(value instanceof Observable) value = value.object
                value = value?."$ex"
            }
            return value
        }
    }

    // TODO: unit test
    void set(bean, String[] expr, newValue) {
        if (expr.size() == 1) {
            throw new IllegalAccessException("${expr[0]} cannot be set")
        } else {
            def value = bean
            def last = expr.size()-1
            for(i in 0..<last) {
                if(value instanceof Observable) value = value.object
                value = value?."$ex"
            }
            value."${expr[last]}" = newValue
        }
    }

    //
    // change occurred from UI
    // assign values directly to beans, not through observable
    //
    def fireViewChanged (Component comp, String eventName) {
        def list = compSubscribeMap[comp]
        for (it in list) {
            String[] expr = it['expr'].split('.')
            def bean = beanMap[expr[0]]
            def value = eval(bean, expr)
            def converter = it['converter']
            def result = converter.coerceToBean(value, comp)
            if(result != TypeConverter.IGNORE)
                set(bean, expr, result)
        }
    }

    boolean contains (Component comp) {
        return beanMap.containsKey(comp)
    }

}

