package zk.hello

import org.zkoss.zk.grails.*

class HelloComposer extends GrailsComposer {

    def afterCompose = { wnd ->
        binds wnd
    }

    def world() {
        alert("Hello World")
    }

    @Listen('#btnHello.onClick') showHello() {
        viewModel.message = "Hello World"
    }

}
