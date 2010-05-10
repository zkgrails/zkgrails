package zk

import org.zkoss.zkgrails.GrailsComposer;

class MyComposer extends GrailsComposer {

    def lblTest
    def myComet

    def afterCompose = {
        myComet.start()
    }

    def onClick_btnStop() {
        myComet.stop()
    }

}
