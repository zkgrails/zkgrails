package zk.hello

import org.zkoss.zk.grails.*

class HelloComposer extends GrailsComposer {

    def afterCompose = { wnd -> binds wnd }

    static mapping = {
        "#/world/$who"()
    }

    def world(who) {
        viewModel.message = "Hello World via Tag with $who"
    }

    @Listen('#btnHello.onClick') showHello() {
        viewModel.message = "Hello World"
    }

}
