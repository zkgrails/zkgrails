package org.zkoss.zk.grails.databind

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.zkoss.zul.Window
import org.zkoss.zul.Textbox

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
        assert binder.containsBean("user")
        assert binder.containsBean("fullname")
        assert binder.containsBean("nameIsLowerCase")
        assert binder.containsBean("userList")
    }

    void testViewModelBinds() {
        def appCtx = grailsApplication.getMainContext()
        def userComposer = appCtx.getBean("zk.userComposer")
        def wnd = new Window(); wnd.id = "wnd"
        def txtName = new Textbox(); txtName.id = "txtName"
        def txtLastName = new Textbox(); txtLastName.id = "txtLastName"
        def txtFullName = new Textbox(); txtFullName.id = "txtFullName"
        wnd.appendChild(txtName)
        wnd.appendChild(txtLastName)
        wnd.appendChild(txtFullName)
        assert wnd.getFellowIfAny("txtName") == txtName
        assert wnd.getFellowIfAny("txtLastName") == txtLastName
        assert wnd.getFellowIfAny("txtFullName") == txtFullName

        userComposer.viewModel.binds(wnd)

        NewDataBinder binder = userComposer.viewModel.getBinder()
        assert binder.contains(txtName)
        assert binder.contains(txtLastName)
        assert binder.contains(txtFullName)

        assert txtName.value == "test"
        assert txtLastName.value == "last"
        assert txtFullName.value == "test last"
    }
}
