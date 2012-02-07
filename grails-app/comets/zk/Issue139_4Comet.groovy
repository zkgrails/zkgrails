package zk

import org.zkoss.zk.grails.GrailsComet

class Issue139_4Comet extends GrailsComet {

    int i = 0

    static trigger = [startDelay: 0L, every: 250L]

    def beforeExecute = { desktop, page ->
        lblTest4.value = "begin"
    }

    def execute = { desktop, page ->
        i++
        lblTest4.value = "time4 : ${i}"

        if(i == 2) {
            i = 0
            stop()
        }
    }

    def afterExecute = { desktop, page ->
        lblTest4.value = "end"
    }
}
