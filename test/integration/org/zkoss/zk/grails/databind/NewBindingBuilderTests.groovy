package org.zkoss.zk.grails.databind

import org.codehaus.groovy.grails.commons.GrailsApplication

class NewBindingBuilderTests extends GroovyTestCase {

    GrailsApplication grailsApplication

    void testWiring() {
        def appCtx = grailsApplication.getMainContext()
        def userComposer = appCtx.getBean("zk.userComposer")
        assert userComposer != null
        assert userComposer.viewModel != null
    }

}
