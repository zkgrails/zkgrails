package zk

import org.zkoss.zk.grails.GrailsComposer;

class MyComposer extends GrailsComposer {

    def lblTest
    def myComet

    def afterCompose = {    }

    def onClick_btnStart() {
        myComet.start()
    }

}
