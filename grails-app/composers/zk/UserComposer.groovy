package zk

import org.zkoss.zk.grails.GrailsComposer

class UserComposer extends GrailsComposer {

    def lblTest
    def myComet

    def afterCompose = { wnd ->
        viewModel binds wnd
    }

}
