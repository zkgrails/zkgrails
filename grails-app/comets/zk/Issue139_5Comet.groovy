package zk

import org.zkoss.zk.grails.GrailsComet

class Issue139_5Comet extends GrailsComet {

    int i = 0

    static trigger = [startDelay: 10000L, every: 5000L]

    def beforeExecute = { desktop, page ->
        lblTest5.value = "begin"
    }

    def execute = { desktop, page ->
        i++
        lblTest5.value = "time5 : ${i}"

        if(i == 5) {
            i = 0
            stop()
        }
    }

    def afterExecute = { desktop, page ->
        lblTest5.value = "end"
    }
}
