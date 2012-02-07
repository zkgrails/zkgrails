package zk

class Issue139_1Comet extends org.zkoss.zk.grails.GrailsComet {

    int i = 0

    static trigger = [startDelay: 800L, every: 1000L]

    def beforeExecute = { desktop, page ->
        lblTest1.value = "begin"
    }

    def execute = { desktop, page ->
        i++
        lblTest1.value = "time1 : ${i}"

        if(i == 2) {
            i = 0
            stop()
        }
    }

    def afterExecute = { desktop, page ->
        lblTest1.value = "end"
    }
}
