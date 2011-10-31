package org.zkoss.zk.grails.databind

import java.lang.reflect.Field
import org.zkoss.zk.grails.GrailsViewModel
import org.zkoss.zk.ui.Component

class NewBindingBuilder {

    GrailsViewModel viewModel
    NewDataBinder binder
    Component root

    NewBindingBuilder(viewModel, binder, root) {
        this.viewModel = viewModel
        this.binder = binder
        this.root = root

        bindBeans()
    }

    private static final ArrayList<String> EXCLUDES = ['id', 'class', 'binder', 'binding', 'metaClass']
    def bindBeans() {
        binder.bindBean(viewModel.id, viewModel)
        viewModel.properties.each { k, v ->
            if (!EXCLUDES.contains(k)) {
                binder.bindBean(k, v)
            }
        }
    }

    //
    // builder method
    // @name is the component's id
    //
    def methodMissing(String name, args) {
        // TODO: use Select to quantify components
        // def components = [root.getFellowIfAny(name)]
        def comp = root.getFellowIfAny(name)
        comp.addEventListener("onChange", this.viewModel)
        def map = args[0]
        map.each { attr, expr ->
            binder.addBinding(comp, attr, expr, ViewModelTypeConverter.instance)
        }
    }

    // must be called after calling static binding = {}
    void subscribeExpressions() {
        // each binding key
        def klass = viewModel.class
        binder.beanMap.each { expr, v ->
            Field f = klass.getDeclaredField(expr)
            def exprToSubscribe = f.getAnnotation(DependsOn.class).expressions()
            if(exprToSubscribe instanceof String) {
                binder.subscribeToExpression(expr, exprToSubscribe)
            } else if(exprToSubscribe instanceof List) {
                exprToSubscribe.each {
                    binder.subscribeToExpression(expr, it)
                }
            }
        }
    }

}
