package zk.hello

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire

class HelloComposer extends GrailsComposer {

    @Wire lblMessage

    def afterCompose = { wnd ->
    }

    static mapping = {
        "#/world/$who"()
    }

    def world(who) {
        if(who) {
            lblMessage.value = "Hello World via Tag with who = $who"
        } else {
            lblMessage.value = "Hello World via Tag"
        }
    }

    @Listen('onClick = #btnHello') showHello() {
        lblMessage.value = "Hello World"
    }

}
