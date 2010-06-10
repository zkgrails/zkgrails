package zk

import org.zkoss.zkgrails.*

class Issue137Comet extends GrailsComet {

    int i = 0

    static trigger = [startDelay: 800L, every: 1000L]

    def beforeExecute = { desktop, page ->
        lblBefore.value = "Before"
    }

    def afterExecute = { desktop, page ->
        lblAfter.value = "After"
    }

    def execute = { desktop, page ->
        i++
        lblTest.value = "time : ${i}"

        if(i == 5) stop()
    }

}
