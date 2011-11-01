package org.zkoss.zk.grails.databind

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.zkoss.zul.Window
import org.zkoss.zul.Textbox
import org.zkoss.zk.grails.GrailsViewModel

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

    void testViewModelBindsAndDependentBinds() {
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

        GrailsViewModel viewModel = userComposer.viewModel
        viewModel.binds(wnd)

        NewDataBinder binder = viewModel.getBinder()
        assert binder.containsComponent(txtName)
        assert binder.containsComponent(txtLastName)
        assert binder.containsComponent(txtFullName)

        assert txtName.value == "test"
        assert txtLastName.value == "last"
        assert txtFullName.value == "test last"

        Set fullnameSubscribeSet = binder.exprSubscribeMap['fullname'] as Set
        Set userNameSubscribeSet = binder.exprSubscribeMap['user.name'] as Set
        Set userLastNameSubscribeSet = binder.exprSubscribeMap['user.lastName'] as Set
        assert fullnameSubscribeSet.every { userNameSubscribeSet.contains(it) }
        assert fullnameSubscribeSet.every { userLastNameSubscribeSet.contains(it) }
    }
}
