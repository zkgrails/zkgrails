package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue139Tests extends GroovyTestCase {

    void testComet() {
        selenium.with {
            setSpeed("800")
            setTimeout("80000")
            open "/zk/issue_139.zul"
            assertEquals "Ready", getText("zk_comp_4")
            assertEquals "Ready", getText("zk_comp_5")
            assertEquals "Ready", getText("zk_comp_6")
            assertEquals "Ready", getText("zk_comp_7")
            assertEquals "Ready", getText("zk_comp_8")

            click "zk_comp_2"

            waitForText("zk_comp_4", "begin")
            waitForText("zk_comp_5", "begin")
            waitForText("zk_comp_6", "begin")
            waitForText("zk_comp_7", "begin")
            waitForText("zk_comp_8", "begin")

            // waitForText("zk_comp_4", "time1 : 5")
            // waitForText("zk_comp_5", "time2 : 5")
            // waitForText("zk_comp_6", "time3 : 5")
            // waitForText("zk_comp_7", "time4 : 5")
            // waitForText("zk_comp_8", "time5 : 5")

            waitForText("zk_comp_4", "end")
            waitForText("zk_comp_5", "end")
            waitForText("zk_comp_6", "end")
            waitForText("zk_comp_7", "end")
            waitForText("zk_comp_8", "end")
        }
    }

}
