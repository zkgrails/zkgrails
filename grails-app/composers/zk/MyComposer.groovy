package zk

import org.zkoss.zkgrails.GrailsComposer;

class MyComposer extends GrailsComposer {

    def lblTest
    def myComet

    def afterCompose = {    }

    def onClick_btnStart() {
        myComet.start()
    }

}
