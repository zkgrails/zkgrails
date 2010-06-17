package zk

import org.zkoss.zkgrails.*

class Issue142Comet extends GrailsComet {

    int i = 0

    static trigger = [startDelay: 800L, every: 1000L]

    def execute = { desktop, page ->
        i++
        lblTest.value = "time : ${i}"
    }

}