package zk

import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.commons.GrailsApplication

class UserViewModelTests extends GrailsUnitTestCase {

    GrailsApplication grailsApplication

    def testFullname() {

        mockDomain(User,[new User(name: "Chanwit", lastName: "Kaewkasi")])

        def appCtx = grailsApplication.getMainContext()
        def userComposer = appCtx.getBean("zk.userComposer")
        def viewModel = userComposer.viewModel
        viewModel.user = User.findByName('Chanwit')
        assert viewModel.fullname['forward']() == "Chanwit Kaewkasi"
        assert viewModel.nameIsLowerCase() == 'color: black'

        viewModel.fullname['reverse']("chanwit kaewkasi")
        assert viewModel.fullname['forward']() == "chanwit kaewkasi"
        assert viewModel.nameIsLowerCase() == 'color: red'

    }

}
