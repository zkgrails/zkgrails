package zk.hello

import org.zkoss.zk.grails.*
import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zk.ui.select.annotation.Listen

class HelloComposer extends GrailsComposer {

    def afterCompose = { wnd -> binds wnd }

    static mapping = {
        "#/world/$who"()
    }

    def world(who) {
        viewModel.message = "Hello World via Tag with $who"
    }

    @Listen('onClick = #btnHello') showHello() {
        viewModel.message = "Hello World"
    }

}
