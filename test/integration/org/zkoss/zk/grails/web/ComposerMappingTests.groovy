package org.zkoss.zk.grails.web;

import static org.junit.Assert.*;

import org.apache.catalina.core.ApplicationContext;
import org.codehaus.groovy.grails.commons.GrailsApplication;

class ComposerMappingTests extends GroovyTestCase {

    GrailsApplication grailsApplication

    void testSomething() {
        def appCtx = grailsApplication.mainContext
        def cm = appCtx.getBean(ComposerMapping.BEAN_NAME)
    }

}
