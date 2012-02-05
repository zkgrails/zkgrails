package zk

import org.zkoss.zk.grails.*
import org.zkoss.zk.grails.composer.GrailsComposer;

class Issue139Composer extends GrailsComposer {

    def issue139_1Comet
    def issue139_2Comet
    def issue139_3Comet
    def issue139_4Comet
    def issue139_5Comet

    def lblTest1
    def lblTest2
    def lblTest3
    def lblTest4
    def lblTest5

    /*def afterCompose = { window ->
        issue139_1Comet.start()
        issue139_2Comet.start()
        issue139_3Comet.start()
        issue139_4Comet.start()
        issue139_5Comet.start()
    }*/

    def onClick_btnStart() {
        issue139_1Comet.start()
        issue139_2Comet.start()
        issue139_3Comet.start()
        issue139_4Comet.start()
        issue139_5Comet.start()
    }
}