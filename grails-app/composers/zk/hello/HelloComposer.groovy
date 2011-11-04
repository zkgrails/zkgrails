package zk.hello

import org.zkoss.zk.grails.GrailsComposer
import org.zkoss.zk.grails.Listen

class HelloComposer extends GrailsComposer {

    def afterCompose = { wnd ->
        binds wnd
    }

    @Listen('#btnHello.onClick') showHello() {
        println "hello"
        viewModel.message = "Hello World"
    }

}
