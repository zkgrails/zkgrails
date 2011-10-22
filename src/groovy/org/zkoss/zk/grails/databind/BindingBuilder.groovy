package org.zkoss.zk.grails.databind

import org.zkoss.zk.ui.Executions

class BindingBuilder {

    static final String BINDING_ARGS = "zkgrails.bindingArgs"

    def binder
    def page
    def viewModel
    def root

    BindingBuilder(viewModel, binder, root) {
        this.viewModel = viewModel
        this.binder   = binder
        this.page = Executions?.current?.currentPage
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
    // @name is a selector pattern
    // @args are a map containing binding information
    //
    def methodMissing(String name, args) {
        // TODO: use Select to quantify components
        def components = [root.getFellowIfAny(name)]
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
                                        this.binder.addBinding(comp, uiProperty, "${viewModel.id}.${source}.${propName}")

                                        break // match each bean property with each comp?
                        case "suffix":  if(comp.id.endsWith(propName))
                                        this.binder.addBinding(comp, uiProperty, "${viewModel.id}.${source}.${propName}")

                                        break // match each bean property with each comp suffix?
                    }
                } else { // normal mode
                    def closureSets = [:]
                    map.each { k, v ->
                        // if v is a field of viewModel
                        def obj = viewModel.properties[v]
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

                        //
                        // an expression is would be something like "viewModel.person.name"
                        //
                        // $k is "value"
                        // $v is user.name
                        this.binder.addBinding(comp, k,
                            "${v}", null, null, "both",
                            "org.zkoss.zk.grails.databind.GrailsTypeConverter")
                    }

                    comp.setAttribute(BindingBuilder.BINDING_ARGS, [binder: this.binder, closureSets: closureSets])
                }
            }
        }
    }

}