@artifact.package@import org.zkoss.zk.grails.*

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.select.annotation.Wire

class @artifact.name@ {

    String message
    @Wire  btnHello

    @Init init() {
        // initialzation code here
    }

    @NotifyChange(['message'])
    @Command clickMe() {
        message = "Clicked"
    }

}
