package zk

import org.zkoss.zkgrails.*

class Issue139Composer extends GrailsComposer {

    def issue139_1Comet
    def issue139_2Comet
    def issue139_3Comet

    def lblTest1
    def lblTest2
    def lblTest3

    def onClick_btnStart() {
        issue139_1Comet.start()
        issue139_2Comet.start()
        issue139_3Comet.start()
    }
}