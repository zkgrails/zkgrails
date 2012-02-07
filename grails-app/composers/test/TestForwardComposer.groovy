package test

import org.zkoss.*
import org.zkoss.zk.ui.event.*
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.grails.composer.GrailsComposer

class TestForwardComposer extends GrailsComposer {

    @Wire wndMain

    def afterCompose = { c ->
        wndMain.append {
            button(id:"btnForwardButton2", label:"Button 2", forward:"onClick=onHandleClick")
        }
    }

    void onHandleClick(Event e) {
        e.origin.target.label = "Clicked"
    }
}