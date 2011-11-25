package zk

import grails.test.GrailsUnitTestCase
import org.zkoss.zk.grails.test.MockViewModel

class UserViewModelTests extends GrailsUnitTestCase {

    def testFullname() {
        mockDomain(User,[new User(name: "Chanwit", lastName: "Kaewkasi")])
        def viewModel = new MockViewModel(UserViewModel)
        viewModel.user = User.findByName('Chanwit')

        assert viewModel.fullname == "Chanwit Kaewkasi"
        assert viewModel.nameIsLowerCase == 'color: black'

        viewModel.fullname = "chanwit kaewkasi"
        assert viewModel.fullname == "chanwit kaewkasi"
        assert viewModel.nameIsLowerCase == 'color: red'
    }

}
