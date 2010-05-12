package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue125Tests extends GroovyTestCase {

    void testComet() {
        selenium.with {
            setSpeed("1000")
            open "/zk/issue_125_test_comet.zul"
            assertEquals "Ready", getText("zk_comp_3")
            click "zk_comp_2"
            waitForText("zk_comp_3", "time : 5")
            assertEquals "time : 5", getText("zk_comp_3")
        }
    }

}
