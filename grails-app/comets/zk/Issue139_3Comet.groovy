package zk

import org.zkoss.zkgrails.*

class Issue139_3Comet extends GrailsComet {

    int i = 0

    static trigger = [startDelay: 800L, every: 1000L]

    def execute = { desktop, page ->
        i++
        lblTest3.value = "time3 : ${i}"

        if(i == 5) stop()
    }

}
