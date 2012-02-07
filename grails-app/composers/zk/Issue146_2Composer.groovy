package zk

import org.zkoss.zk.grails.composer.GrailsComposer;

class Issue146_2Composer extends GrailsComposer {
    static skipZscriptWiring = true

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