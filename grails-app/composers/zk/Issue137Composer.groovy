package zk

import org.zkoss.zk.grails.*
import org.zkoss.zk.grails.composer.GrailsComposer;

class Issue137Composer extends GrailsComposer {

    def issue137Comet

    def lblBefore
    def lblAfter
    def lblTest
    
    def onClick_btnStart() {
        issue137Comet.start()
    }
}