package zk

class MyComet extends org.zkoss.zk.grails.GrailsComet {

    int i = 0

    static trigger = [startDelay: 800L, delay: 1000L]

    def execute = { desktop, page ->
        i++
        lblTest.value = "time : ${i}"

        if(i == 5) stop()
    }
}
