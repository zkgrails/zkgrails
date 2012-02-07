package zk

import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue146Tests extends GroovyTestCase {


    // true on Config, nothing on composer, will skip zscript wiring
    void testSkipZscriptWiringFromGlobalConfig() {
        /* deprecated
        AH.application.config.grails.zk.skipZscriptWiring = true
        selenium.with {
            setSpeed "1000"
            open "/zk/issue_146_1.zul"
            assertEquals "hi from zscript", getText("zk_comp_4")
            click "zk_comp_2"
            waitForText("zk_comp_4", "skipped wiring")
        }
        AH.application.config.grails.zk.skipZscriptWiring = null
        */
    }

    // nothing on Config, true on composer, will skip zscript wiring
    void testSkipZscriptWiringFromComposerConfig() {
        /* deprecated
        AH.application.config.grails.zk.skipZscriptWiring = null
        selenium.with {
            setSpeed "1000"
            open "/zk/issue_146_2.zul"
            assertEquals "hi from zscript", getText("zk_comp_4")
            click "zk_comp_2"
            waitForText("zk_comp_4", "skipped wiring")
        }
        */
    }

    // true on Config, false on composer, will wire zscript (don't skip)
    void testPerComposerPrecedenceOverGlobalConfig() {
        /* deprecated
        AH.application.config.grails.zk.skipZscriptWiring = true
        selenium.with {
            setSpeed "1000"
            open "/zk/issue_146_3.zul"
            assertEquals "hi from zscript", getText("zk_comp_4")
            click "zk_comp_2"
            waitForText("zk_comp_4", "zscript wired")
        }
        AH.application.config.grails.zk.skipZscriptWiring = null
        */
    }

    // nothing on Config, nothing on Composer, will wire zscript (don't skip)
    void testZscriptWiringDefault() {
        /* deprecated
        AH.application.config.grails.zk.skipZscriptWiring = null
        selenium.with {
            setSpeed "1000"
            open "/zk/issue_146_4.zul"
            assertEquals "hi from zscript", getText("zk_comp_4")
            click "zk_comp_2"
            waitForText("zk_comp_4", "zscript wired")
        }
        */
    }

}
