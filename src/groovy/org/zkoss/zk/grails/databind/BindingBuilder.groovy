package org.zkoss.zk.grails.databind

import org.zkoss.zk.ui.Executions
import org.zkoss.zk.grails.jsoup.select.Selector

import org.codehaus.groovy.grails.commons.GrailsClassUtils

class BindingBuilder {

    static final String BINDING_ARGS = "zkgrails.bindingArgs"

    def binding
    def page
    def viewModel
    def root

    BindingBuilder(viewModel, binding, root) {
        this.viewModel = viewModel
        this.binding   = binding
        this.page = Executions?.current?.currentPage
        this.root = root
    }

    //
    // @name is a selector pattern
    // @args are a map containing binding information
    //
    def methodMissing(String name, args) {

        // def components = page?.roots.collect { root -> root.getFellowIfAny(name) }
        // if(components?.size() == 0) {
        //    components = Selector.select(name, page?.roots)
        // }

        def components = Selector.select(name, root)

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
                        case "normal":  if(comp.id == propName)
                                        this.binding.addBinding(comp, uiProperty, "${viewModel.id}.${source}.${propName}")

                                        break // match each bean property with each comp?
                        case "suffix":  if(comp.id.endsWith(propName))
                                        this.binding.addBinding(comp, uiProperty, "${viewModel.id}.${source}.${propName}")

                                        break // match each bean property with each comp suffix?
                    }
                } else { // normal mode
                    closureSets = [:]
                    args.each { k, v ->
                        // if v is a field of viewModel
                        def obj = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(viewModel, v)
                        if (obj) {
                            if(obj instanceof Map) {
                                if(obj.containsKey('forward')) {
                                    closureSets["${k}:forward"] = obj['forward']
                                } else if(obj.containsKey('reverse')) {
                                    closureSets["${k}:reverse"] = obj['reverse']
                                }
                            } else if(obj instanceof Closure) {
                                closureSets["${k}:forward"] = obj
                            }
                        }
                        // and v is a map containing forward || reverse
                        // set value of v as a converter
                        // else

                        //
                        // an expression is would be something like "viewModel.person.name"
                        //
                        this.binding.addBinding(comp, k,
                            "${viewModel.id}.${v}", null, null, null,
                            "org.zkoss.zk.grails.databind.GrailsTypeConverter")
                    }
                    comp.setAttribute(BINDING_ARGS, [args: args, viewModel: viewModel, closureSets: closureSets])
                }
            }
        }
        this.binding.bindBean(this.viewModel.id, this.viewModel)
        this.binding.loadAll()
    }

}