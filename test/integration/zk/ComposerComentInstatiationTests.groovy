package zk;

import org.codehaus.groovy.grails.commons.DefaultGrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsApplication;

import groovy.util.GroovyTestCase;

class ComposerComentInstatiationTests extends GroovyTestCase {

    GrailsApplication grailsApplication

    void testWiring() {
        def appCtx = grailsApplication.getMainContext()
        def myComposer = appCtx.getBean("zk.myComposer")
        myComposer.injectComet()
        assert myComposer != null
        assert myComposer.myComet != null
        assert myComposer.myComet.grailsComposer != null
    }

}
