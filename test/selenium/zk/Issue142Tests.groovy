package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue142Tests extends GroovyTestCase {

    void testComet() {
        selenium.with {
            setSpeed("1000")
            open "/zk/issue_142.zul"
            assertEquals "Ready", getText("zk_comp_4")
            click "zk_comp_2" // start
            waitForText("zk_comp_5", "time : 5")
            click "zk_comp_3" // stop
            click "zk_comp_2" // start
            waitForText("zk_comp_5", "time : 15")
            click "zk_comp_3" // stop
        }
    }

}
