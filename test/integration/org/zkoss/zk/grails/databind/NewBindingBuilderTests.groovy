package org.zkoss.zk.grails.databind

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.zkoss.zul.Window

class NewBindingBuilderTests extends GroovyTestCase {

    GrailsApplication grailsApplication

    void testWiring() {
        def appCtx = grailsApplication.getMainContext()
        def userComposer = appCtx.getBean("zk.userComposer")
        assert userComposer != null
        assert userComposer.viewModel != null
    }

    void testInstantiateNewBindingBuilder() {
        def appCtx = grailsApplication.getMainContext()
        def userComposer = appCtx.getBean("zk.userComposer")
        def binder = new NewDataBinder()
        def comp = new Window()
        def nbb = new NewBindingBuilder(userComposer.viewModel, binder, comp)
        assert nbb.root == comp
        assert binder.getBean("userViewModel") == userComposer.viewModel
    }

}
