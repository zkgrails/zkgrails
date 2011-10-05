package org.zkoss.zk.grails.databind

import org.zkoss.zk.ui.Executions
import org.zkoss.zk.grails.jsoup.select.Selector

class BindingBuilder {

    def binding
    def page
    def viewModel

    BindingBuilder(viewModel, binding) {
        this.viewModel = viewModel
        this.binding   = binding
        this.page = Executions?.current?.currentPage
    }

    //
    // @name is a selector pattern
    // @args are a map containing binding information
    //
    def methodMissing(String name, args) {

        def result = page?.roots.collect { root -> root.getFellowIfAny(name) }
        if(result?.size() == 0) {
            result = Selector.select(name, page?.roots)
        }
        if(result?.size() == 0) {
            throw new MissingMethodException(name, args)
        }

        result.each { comp ->
            if(args instanceof Map) {
                //
                // args['property'] is a property name to bind
                // expression is would be "viewModel.person.name"
                //
                this.binding.addBinding(comp, args['property'], "${this.viewModel.id}.${args['']}" )
                // binder.addBinding(editor, "value", "selected.${p.name}")
                // set data binding
                // binding.
            }
        }
        this.binding.bindBean(this.viewModel.id, this.viewModel)
        this.binding.loadAll()
    }

}