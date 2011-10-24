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
    def exprSubscribeMap = [:]
    def compSubscribeMap = [:]

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

    def eval(String expression) {
        String[] expr = expression.split(/\./)
        def bean = beanMap[expr[0]]
        if (expr.size() == 1) {
            def value = bean
            if(value instanceof Observable) value = value.object
            return value
        } else {
            def value = bean
            for(ex in expr[1..-1]) {
                if(value instanceof Observable) value = value.object
                value = value?."$ex"
            }
            return value
        }
    }

    void set(String expression, newValue) {
        String[] expr = expression.split(/\./)
        def bean = beanMap[expr[0]]
        if (expr.size() == 1) {
            throw new IllegalAccessException("${expr[0]} cannot be set")
        } else {
            def value = bean
            def last = expr.size()-1
            for(i in 1..<last) {
                if(value instanceof Observable) value = value.object
                value = value?."$expr[i]"
            }
            if(value instanceof Observable) value = value.object
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
            def converter = it['converter']
            def expr = it['expr']
            def value = eval(expr)
            def result = converter.coerceToBean(value, comp)
            if(result != TypeConverter.IGNORE)
                set(expr, result)
        }
    }

    boolean contains (Component comp) {
        return compSubscribeMap.containsKey(comp)
    }

    def getBean(String key) {
        return beanMap[key]
    }
}

