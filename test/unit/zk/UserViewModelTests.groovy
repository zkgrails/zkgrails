package zk

import grails.test.GrailsUnitTestCase

import org.zkoss.zk.grails.GrailsViewModel;
import org.zkoss.zk.grails.databind.DependsOn;
import org.zkoss.zk.grails.test.MockViewModel

class UserViewModelTests extends GrailsUnitTestCase {

    def testTransformation() {
        def c = UserViewModel.class
        assert c.superclass == GrailsViewModel.class
        def fullname = c.getDeclaredField('fullname')
        assert fullname.getAnnotation(DependsOn.class).value() == ['user.name', 'user.lastName']
        def colorForName = c.getDeclaredField('colorForName')
        assert colorForName.getAnnotation(DependsOn.class).value() == ['user.name']
    }

    def testFullname() {
        mockDomain(User,[new User(name: "Chanwit", lastName: "Kaewkasi")])
        def viewModel = new MockViewModel(UserViewModel)
        viewModel.user = User.findByName('Chanwit')

        assert viewModel.fullname == "Chanwit Kaewkasi"
        assert viewModel.colorForName == 'color: black'

        viewModel.fullname = "chanwit kaewkasi"
        assert viewModel.fullname == "chanwit kaewkasi"
        assert viewModel.colorForName == 'color: red'
    }

}
