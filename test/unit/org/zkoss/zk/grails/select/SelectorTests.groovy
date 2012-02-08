package org.zkoss.zk.grails.select

import org.zkoss.zul.Window
import org.zkoss.zul.Button

import org.zkoss.zul.Vbox
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.select.Selectors

class SelectorTests extends GroovyTestCase {

    def testIdSelector() {
        def wnd = new Window(id: 'wnd')
        def button = new Button(id:'test')
        wnd.appendChild(button)
        def a = Selectors.find(wnd, '#test')
        assert a.size()==1
        assert a[0] == button
        assert a[0].id == 'test'
    }

    def testMultipleResults() {
        def wnd = new Window(id: 'wnd')
        def b1  = new Button(id: 'test')
        def b2  = new Button(id: 'test2')
        wnd.appendChild(b1)
        wnd.appendChild(b2)
        def a = Selectors.find(wnd, 'button')
        assert a.size()==2
        assert a[0] == b1
        assert a[1] == b2
    }

    def testSelectChildren() {
        def wnd = new Window(id: 'wnd')
        def b1  = new Button(id: 'test')
        def b2  = new Button(id: 'test2')
        def b3  = new Button(id: 'outter')
        def vbox = new Vbox([b1, b2] as Component[])
        vbox.id = 'box'
        wnd.appendChild(vbox)
        wnd.appendChild(b3)
        def a = Selectors.find(wnd, '#box > button')
        assert a.size()==2
        assert a[0] == b1
        assert a[1] == b2
    }

}
