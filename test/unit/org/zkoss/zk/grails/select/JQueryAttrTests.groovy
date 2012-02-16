package org.zkoss.zk.grails.select

import org.zkoss.zul.Window
import org.zkoss.zul.Button

import org.zkoss.zul.Vbox
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.select.Selectors

class JQueryAttrTests extends GroovyTestCase {

    def testJQueryAttr_0() {
        JQuery jq = new JQuery([new Button(id:'myButton')])
        assert jq.components[0].id == 'myButton'
        assert jq.attr('id') == 'myButton'
    }

    def testJQueryAttr_1() {
        Button b1 = new Button(id:'myButton1',label:'OK')
        Button b2 = new Button(id:'myButton2',label:'Cancel')
        JQuery jq = new JQuery([b1, b2])
        assert jq.components[0].id == 'myButton1'
        assert jq.components[1].id == 'myButton2'
        jq.attr('label', 'changed')
        assert b1.label == 'changed'
        assert b2.label == 'changed'
    }

}
