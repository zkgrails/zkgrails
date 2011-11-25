package org.zkoss.zk.grails.databind

import org.zkoss.zk.ui.Component
import org.zkoss.zkplus.databind.TypeConverter
import org.zkoss.zk.grails.GrailsViewModel

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
//      this explains by event firing
//

class DataBinder {

    static final String ZKGRAILS_BINDING_CONTEXT = "zk.grails.binding.context"
    def beanMap = [:]
    def exprSubscribeMap = [:]
    def compSubscribeMap = [:]
    private GrailsViewModel viewModel
    def validate = true

    DataBinder(viewModel) {
        this.viewModel = viewModel
    }

    //
    //  context
    //  this case should be ViewModel ?
    //
    def bindBean(id, bean) {
        // TODO support collection
        if(bean instanceof Observable)
            beanMap[id] = new Observable(bean.object, this, id)
        else
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
        exprSubscribeMap[exprToSubscribe].addAll(exprSubscribeMap[expr])
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
                comp.setAttribute(ZKGRAILS_BINDING_CONTEXT, [binder: this, viewModel: this.viewModel, expr: expr])
                def result = converter.coerceToUi(newValue, comp)
                comp.removeAttribute(ZKGRAILS_BINDING_CONTEXT)
                if(result != TypeConverter.IGNORE)
                    setComp(comp, attr, result)

            } else {
                setComp(comp, attr, value)
            }
        }
    }

    def setComp(comp, attr, value) {
        if(value instanceof Map && value.containsKey(attr))
            comp."${attr}" = value[attr]
        else
            comp."${attr}" = value
    }

    def eval(String expression) {
        if(!expression.contains('.')) expression = viewModel.id + '.' + expression
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
        if(!expression.contains('.')) expression = viewModel.id + '.' + expression
        // println "set expression: ${expression}"
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
            if(bean.metaClass.hasProperty(bean, "validate") && validate) {
                bean.validate()
            }
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
            comp.setAttribute(ZKGRAILS_BINDING_CONTEXT, [binder: this, viewModel: this.viewModel, expr: expr])
            def result = converter.coerceToBean(value, comp)
            comp.removeAttribute(ZKGRAILS_BINDING_CONTEXT)
            if(result != TypeConverter.IGNORE)
                set(expr, result)
        }
    }

    boolean containsComponent(Component comp) {
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

    // read only property
    def propertyMissing(String name) {
        return beanMap[name]
    }

}


