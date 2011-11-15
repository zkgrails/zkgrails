package zk.hello

import org.zkoss.zk.grails.*

class HelloComposer extends GrailsComposer {

    def afterCompose = { wnd ->
        binds wnd
    }

    def world() {
        viewModel.message = "Hello World via Tag"
    }

    @Listen('#btnHello.onClick') showHello() {
        viewModel.message = "Hello World"
    }

}
