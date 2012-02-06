package zk.hello

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.NotifyChange

class HelloViewModel {

    String message = "default text"

    @NotifyChange(['message'])
    @Command buttonClicked() {
        message = "hello"
    }

}
