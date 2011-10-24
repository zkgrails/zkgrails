package org.zkoss.zk.grails.databind

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

        this.binder.bindBean(this.viewModel.id, this.viewModel)
        def excludes = ['id','class','binder','binding','metaClass']
        this.viewModel.properties.each { k, v ->
            if(!excludes.contains(k)) {
                this.binder.bindBean(k, v)
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
        def map = args[0]
        map.each { attr, expr ->
            binder.addBinding(comp, attr, expr, new GrailsTypeConverter())
        }
    }

}
