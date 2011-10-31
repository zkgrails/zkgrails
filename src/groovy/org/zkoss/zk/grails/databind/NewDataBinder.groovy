package org.zkoss.zk.grails.databind

import org.zkoss.zk.ui.Component
import org.zkoss.zkplus.databind.TypeConverter

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

    private static final String ZKGRAILS_BINDING_CONTEXT = "zk.grails.binding.context"
    def beanMap = [:]
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
    // subscribe to expression (Observable)
    //
    def addBinding(Component comp, String attr, String expr, TypeConverter converter) {
        if(!exprSubscribeMap[expr]) exprSubscribeMap[expr] = new HashSet()
        if(!compSubscribeMap[comp]) compSubscribeMap[comp] = new HashSet()
        def entry = new Tuple(comp: comp, attr: attr, expr: expr, converter: converter)
        exprSubscribeMap[expr] << entry
        compSubscribeMap[comp] << entry
    }

    //
    // subscribe to expression (Dependent Observable)
    //
    def subscribeToExpression(String expr, String exprToSubscribe) {
        if(!exprSubscribeMap[exprToSubscribe]) exprSubscribeMap[exprToSubscribe] = new HashSet()
        exprSubscribeMap[exprToSubscribe] += exprSubscribeMap[expr]
    }

    //
    // firing event from "domain" to "comp"
    // TODO: dealing with sender to prevent SOE
    //
    def fireModelChanged(sender, String expr, value) {
        def list = exprSubscribeMap[expr].collect{ it.comp }.collect { compSubscribeMap[it] }.flatten() // .unique()
        list.each { entry ->
            def attr = entry.attr
            Component comp = entry.comp
            def converter  = entry.converter
            def newValue   = eval(entry.expr)
            if(converter) {
                // TODO: what to put into binding context
                comp.setAttribute(ZKGRAILS_BINDING_CONTEXT,[:])
                def result = converter.coerceToUi(newValue, comp)
                comp.removeAttribute(ZKGRAILS_BINDING_CONTEXT)
                if(result != TypeConverter.IGNORE)
                    comp."${attr}" = result
            } else {
                comp."${attr}" = value
            }
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
        // simply ignore
        // if (expr.size() == 1) {
        //
        // } else
        if (expr.size() > 1) {
            def value = bean
            def last = expr.size()-1
            for(i in 1..<last) {
                if(value instanceof Observable) value = value.object
                value = value?."$expr[i]"
            }
            //
            // TODO if the below causes SOE bug, remove the comment
            // if(value instanceof Observable) value = value.object
            value."${expr[last]}" = newValue
        }
    }

    //
    // change occurred from UI
    // assign values directly to beans, not through observable
    //
    def fireViewChanged (Component comp, String eventName) {
        def list = compSubscribeMap[comp]
        for (entry in list) {
            def converter = entry.converter
            def attr = entry.attr
            def expr = entry.expr
            def value = comp."$attr"
            // TODO: what to put into binding context
            comp.setAttribute(ZKGRAILS_BINDING_CONTEXT,[:])
            def result = converter.coerceToBean(value, comp)
            comp.removeAttribute(ZKGRAILS_BINDING_CONTEXT)
            if(result != TypeConverter.IGNORE)
                set(expr, result)
        }
    }

    boolean contains (Component comp) {
        return compSubscribeMap.containsKey(comp)
    }

    def getBean(String key) {
        return beanMap[key].object
    }

    def containsBean(String beanName) {
        return beanMap.containsKey(beanName)
    }

    public void loadAll( ) {
        // TODO re-implement this. It should be simpler
        exprSubscribeMap.each { expr, list ->
            fireModelChanged(null, expr, eval(expr))
        }
    }

}


