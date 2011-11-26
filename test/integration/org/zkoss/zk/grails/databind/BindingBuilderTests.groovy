package org.zkoss.zk.grails.databind

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.zkoss.zul.Window
import org.zkoss.zul.Textbox
import org.zkoss.zk.grails.GrailsViewModel

class BindingBuilderTests extends GroovyTestCase {

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
        def binder = new DataBinder()
        def comp = new Window()
        def nbb = new BindingBuilder(userComposer.viewModel, binder, comp)
        assert nbb.root == comp
        assert binder.getBean("userViewModel") == userComposer.viewModel.object
        assert binder.containsBean("user")
        assert binder.containsBean("fullname")
        assert binder.containsBean("colorForName")
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

        GrailsViewModel viewModel = userComposer.viewModel.object
        viewModel.binds(wnd)

        DataBinder binder = viewModel.getBinder()
        assert binder.containsComponent(txtName)
        assert binder.containsComponent(txtLastName)
        assert binder.containsComponent(txtFullName)

        assert txtName.value == "test"
        assert txtLastName.value == "last"
        assert txtFullName.value == "test last"

        def fullnameSubscribeSet = binder.exprSubscribeMap['fullname']
        def userNameSubscribeSet = binder.exprSubscribeMap['user.name']
        def userLastNameSubscribeSet = binder.exprSubscribeMap['user.lastName']
        assert fullnameSubscribeSet.every { userNameSubscribeSet.contains(it) }
        assert fullnameSubscribeSet.every { userLastNameSubscribeSet.contains(it) }
    }

    def testBindBeansNotContainExcludedBeans() {
        def appCtx = grailsApplication.getMainContext()
        def userComposer = appCtx.getBean("zk.userComposer")
        def binder = new DataBinder()
        def comp = new Window()
        def nbb = new BindingBuilder(userComposer.viewModel, binder, comp)
        assert nbb.root == comp
        assert binder.getBean("userViewModel") == userComposer.viewModel.object
        assert !binder.containsBean("class")
        assert !binder.containsBean("metaClass")
        assert !binder.containsBean("id")
        assert !binder.containsBean("binder")
        assert !binder.containsBean("binding")
    }
}
