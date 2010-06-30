package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue137Tests extends GroovyTestCase {

    void testComet() {
        selenium.with {
            setSpeed("1000")
            open "/zk/issue_137.zul"
            assertEquals "Ready", getText("zk_comp_4")
            assertEquals "Ready", getText("zk_comp_5")
            assertEquals "Ready", getText("zk_comp_6")
            click "zk_comp_2"
            waitForText("zk_comp_4", "Before")
            waitForText("zk_comp_5", "time : 5")
            waitForText("zk_comp_6", "After")
        }
    }

}
