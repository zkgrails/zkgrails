package org.zkoss.zk.grails

import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.commons.GrailsApplication
import zk.UserComposer
import org.zkoss.zul.*

/**
 * Created by IntelliJ IDEA.
 * User: chanwit
 * Date: 11/5/11
 * Time: 7:59 PM
 * To change this template use File | Settings | File Templates.
 */
class QueryWithDollarSignTests extends GrailsUnitTestCase {

    GrailsApplication grailsApplication

    void testQuery() {
        def appCtx = grailsApplication.getMainContext()
        UserComposer userComposer = appCtx.getBean("zk.userComposer")
        assert userComposer != null

        ZkBuilder.ZKNODES['button'] = Button.class

        Window wnd = new Window(id:'wnd')
        Vbox buttonHolder = new Vbox(id:'buttonHolder')
        wnd.appendChild(buttonHolder)

        userComposer.root = wnd
        buttonHolder.append {
            button(label:"B1.1")
            button(label:"B1.2")
            button(label:"B1.3")
        }
        assert userComposer.$('#buttonHolder > button').size() == 3
    }

}
