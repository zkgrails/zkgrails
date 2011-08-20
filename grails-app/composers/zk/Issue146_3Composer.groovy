package zk

import org.zkoss.zk.grails.GrailsComposer

class Issue146_3Composer extends GrailsComposer {
    static skipZscriptWiring = false

    def strZscript
    def lblZscriptWireResult

    void onClick_btnTestWiring() {
        if(strZscript == 'hi from zscript') {
            strZscript = 'zscript wired'
            lblZscriptWireResult.value = strZscript
        } else {
            lblZscriptWireResult.value = 'skipped wiring'
        }
    }
}