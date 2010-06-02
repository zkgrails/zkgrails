package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue113Tests extends GroovyTestCase {

    void test_UTF8_Display() {
        selenium.with {
            setSpeed "1000"
            open "/zk/issue_113.zul"
            assertEquals "版本1.0", getText("zk_comp_2")
            assertEquals "说明",    getText("zk_comp_3")
        }
    }

}
