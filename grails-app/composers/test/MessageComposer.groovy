package test

import org.zkoss.*
import org.zkoss.zk.ui.event.*
import org.zkoss.zk.grails.*
import org.zkoss.zk.grails.composer.GrailsComposer;

class MessageComposer extends GrailsComposer {

    def btnMessage
    def btnMessage2

    def afterCompose = { c ->
    }

    void onClick_btnMessage() {
        btnMessage.label = message["my.message.click"]
    }

    void onClick_btnMessage2() {
        btnMessage2.label = message(code:"my.message.click2", args:["test"])
    }
}