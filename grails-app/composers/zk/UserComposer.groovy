package zk

import org.zkoss.zk.grails.GrailsComposer

class UserComposer extends GrailsComposer {

    def afterCompose = { wnd ->
        viewModel?.binds wnd
    }

}
