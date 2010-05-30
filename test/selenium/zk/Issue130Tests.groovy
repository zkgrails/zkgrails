package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue130Tests extends GroovyTestCase {

    void testChineseDisplay() {
        selenium.with {
            setSpeed "1000"
            open "/zk/issue_130.zul"
            click "zk_comp_2"
            assertEquals "Clicked", getText("zk_comp_2")
            click "zk_comp_3"
            assertEquals "Clicked", getText("zk_comp_3")
        }
    }

}
