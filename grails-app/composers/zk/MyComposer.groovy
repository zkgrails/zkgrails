package zk

import org.zkoss.zk.grails.composer.GrailsComposer;

class MyComposer extends GrailsComposer {

    def lblTest
    def myComet

    def afterCompose = {    }

    def onClick_btnStart() {
        println "start"
        myComet.start()
    }

}
