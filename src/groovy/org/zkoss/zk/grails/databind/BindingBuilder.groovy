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

        def components = page?.roots.collect { root -> root.getFellowIfAny(name) }
        if(components?.size() == 0) {
            components = Selector.select(name, page?.roots)
        }
        if(components?.size() == 0) {
            throw new MissingMethodException(name, args)
        }

        components.each { comp ->
            if(args instanceof Map) {
                if(args.containsKey('autowire')) {
                    //
                    // autowire mode
                    //  suffix = match firstName <-> txtFirstName
                    //  normal = match firstName <-> firstName
                    //
                    def autowire   = args['autowire'] // "suffix", "normal"
                    def source     = args['source']   // ex. "person"
                    def uiProperty = args['property'] // ex. "value"
                    //
                    // TODO: where to get propName
                    //
                    switch(autowire) {
                        "normal":   if(comp.id == propName)
                                        this.binding.addBinding(comp, uiProperty, "${viewModel.id}.${source}.${propName}")

                                    break// match each bean property with each comp?

                        "suffix":   
                                    if(comp.id.endsWith(propName))
                                        this.binding.addBinding(comp, uiProperty, "${viewModel.id}.${source}.${propName}")

                                    break// match each bean property with each comp suffix?
                    }
                } else {

                    //
                    // normal mode
                    // an expression is would be something like "viewModel.person.name"
                    //
                    args.each { k, v ->
                        this.binding.addBinding(comp, k, "${viewModel.id}.${v}")
                    }
                }
            }
        }
        this.binding.bindBean(this.viewModel.id, this.viewModel)
        this.binding.loadAll()
    }

}