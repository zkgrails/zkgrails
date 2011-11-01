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
        comp.addEventListener("onChange", this.viewModel) // TODO tackle other events
        def map = args[0]
        map.each { attr, expr ->
            binder.addBinding(comp, attr, expr, ViewModelTypeConverter.instance)
        }
    }

    // must be called after calling static binding = {}
    // TODO do topology sorting before calling this to ensure that everything will be in its place
    void subscribeDependentExpressions() {
        // each binding key
        def klass = viewModel.class
        binder.beanMap.each{ expr, v ->
            try {
                Field f = klass.getDeclaredField(expr)
                def exprToSubscribe = f.getAnnotation(DependsOn.class)?.value()
                exprToSubscribe?.each { binder.subscribeToExpression(expr, it) }
            } catch(NoSuchFieldException e){ /* silently skip if it's NSFE */ }
        }
    }

}
