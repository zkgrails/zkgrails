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

        def result = Selector.select(name, page?.roots)

        //
        // TODO: test result null or size == 0 ?
        //
        if(!result) {
            throw new MissingMethodException(name, args)
        }

        result.each {
            // set data binding
            // binding.
        }
        this.binding.bindBean(this.viewModel.id, this.viewModel)
        this.binding.loadAll()
    }

}