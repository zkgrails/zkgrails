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
        
        this.binding.bindBean(this.viewModel.id, this.viewModel)
        def excludes = ['id','class','binder','binding','metaClass']
        this.viewModel.properties.each { k, v ->
            if(!excludes.contains(k)) {
                this.binding.bindBean(k, v)
            }
        }
        
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
        def components = [root.getFellowIfAny(name)]
        //def components = Selector.select(name, root)

        //if(components?.size() == 0) {
        //    throw new MissingMethodException(name, args)
        //}

        components.each { comp ->
            def map = args[0]
            if(map instanceof Map) {
                if(map.containsKey('autowire')) {
                    //
                    // autowire mode
                    //  suffix = match firstName <-> txtFirstName
                    //  normal = match firstName <-> firstName
                    //
                    def autowire   = map['autowire'] // "suffix", "normal"
                    def source     = map['source']   // ex. "person"
                    def uiProperty = map['property'] // ex. "value"
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
                    def closureSets = [:]
                    map.each { k, v ->
                        // if v is a field of viewModel
                        def obj = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(viewModel, v)
                        if (obj) {
                            if(obj instanceof Map) {
                                if(obj.containsKey('forward')) {
                                    closureSets["${v}:forward"] = obj['forward']
                                }
                                if(obj.containsKey('reverse')) {
                                    closureSets["${v}:reverse"] = obj['reverse']
                                }
                            } else if(obj instanceof Closure) {
                                closureSets["${v}:forward"] = obj
                            }
                        }
                        // and v is a map containing forward || reverse
                        // set value of v as a converter
                        // else

                        //
                        // an expression is would be something like "viewModel.person.name"
                        //
                        // $k is "value"
                        // $v is user.name
                        this.binding.addBinding(comp, k,
                            "${v}", null, null, null,
                            "org.zkoss.zk.grails.databind.GrailsTypeConverter")
                    }
                    //
                    // TODO remove unrequire values
                    //
                    comp.setAttribute(BindingBuilder.BINDING_ARGS, [args: args, viewModel: viewModel, closureSets: closureSets])
                }
            }
        }
    }

}