package org.zkoss.zk.grails

import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Window
import zk.hangman.HangmanComposer
import org.zkoss.zul.Vbox
import org.zkoss.zul.Button
import java.lang.reflect.Method
import org.zkoss.util.Pair


class SelectorBasedHandlerTests extends GrailsUnitTestCase {

    GrailsApplication grailsApplication

    void testWiring() {
        def appCtx = grailsApplication.getMainContext()
        HangmanComposer hangmanComposer = appCtx.getBean("zk.hangman.hangmanComposer")
        assert hangmanComposer != null
        Window wnd = new Window(id: "wnd")
        def buttonRow1 = new Vbox(id:"buttonRow1")
        def buttonRow2 = new Vbox(id:"buttonRow2")
        wnd.appendChild(buttonRow1)
        wnd.appendChild(buttonRow2)
        def button1 = new Button(label:'test')
        buttonRow1.appendChild(button1)

        Selectors.wireComponents(wnd, hangmanComposer, true)

        def pair1 = new Pair(button1,"onClick")
        def methods = hangmanComposer.getSelectorBasedHandler(pair1)
        assert methods[0].name == "guess"
    }

}
