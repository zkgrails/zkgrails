package org.zkoss.zk.grails.select

import org.zkoss.zul.Window
import org.zkoss.zul.Button

import org.zkoss.zul.Vbox
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.select.Selectors

class JQueryAddClassTests extends GroovyTestCase {

    def testJQueryAddClass_0() {
        JQuery jq = new JQuery([new Button(sclass:null)])
        assert jq.components[0].sclass == null
        assert jq.addClass("blue").components[0].sclass == "blue"
    }

    def testJQueryAddClass_1() {
        JQuery jq = new JQuery([new Button(sclass:"")])
        assert jq.components[0].sclass == null
        assert jq.addClass("blue").components[0].sclass == "blue"
    }

    def testJQueryAddClass_2() {
        JQuery jq = new JQuery([new Button(sclass:"  ")])
        assert jq.components[0].sclass == "  "
        assert jq.addClass("blue").components[0].sclass == "blue"
    }

    def testJQueryAddClass_3() {
        JQuery jq = new JQuery([new Button(sclass:"red")])
        assert jq.components[0].sclass == "red"
        assert jq.addClass("blue").components[0].sclass == "red blue"
    }

}
