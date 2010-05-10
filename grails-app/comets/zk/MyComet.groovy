package zk

import org.zkoss.zkgrails.*

class MyComet extends GrailsComet {

    int i

    static trigger = [startDelay: 3000L, delay: 1000L]

    def execute = { desktop, page ->
        i++
        lblTest.value = "time : ${i}"
    }
}
