package org.zkoss.zk.grails.select

import org.zkoss.zul.Window
import org.zkoss.zul.Button

import org.zkoss.zul.Vbox
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.select.Selectors

class JQueryRemoveClassTests extends GroovyTestCase {

    def testJQueryRemoveClass_0() {
        JQuery jq = new JQuery([new Button(sclass:"red blue")])
        assert jq.components[0].sclass == "red blue"
        assert jq.removeClass("blue").components[0].sclass == "red"
    }

    def testJQueryRemoveClass_1() {
        JQuery jq = new JQuery([new Button(sclass:"red green blue")])
        assert jq.components[0].sclass == "red green blue"
        assert jq.removeClass("green").components[0].sclass == "red blue"
    }

    def testJQueryRemoveClass_2() {
        JQuery jq = new JQuery([new Button(sclass:"")])
        assert jq.components[0].sclass == null
        assert jq.removeClass("green").components[0].sclass == null
    }

    def testJQueryRemoveClass_3() {
        JQuery jq = new JQuery([new Button(sclass:"  ")])
        assert jq.components[0].sclass == "  "
        assert jq.removeClass("green").components[0].sclass == null
    }

}
