package org.zkoss.zk.grails.databind

import java.lang.reflect.Field
import org.zkoss.zk.grails.GrailsViewModel
import org.zkoss.zk.grails.composer.databind.DependsOn;
import org.zkoss.zk.ui.Component

class BindingBuilder {

    GrailsViewModel viewModel
    DataBinder binder
    Component root

    BindingBuilder(viewModel, binder, root) {
        if(viewModel instanceof Observable) {
            this.viewModel = viewModel.object
        } else {
            this.viewModel = viewModel
        }
        this.binder = binder
        this.root = root

        bindBeans()
    }

    def bindBeans() {
        binder.bindBean(viewModel.id, viewModel)
        viewModel.properties.each { k, v ->
            if (!(k in GrailsViewModel.EXCLUDES)) {
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
