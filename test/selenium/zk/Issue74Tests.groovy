package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue74Tests extends GroovyTestCase {

    void testSession() {
        selenium.with {
            setSpeed "1000"
            open "/zk/issue_74.zul"
            // user label
            assertEquals "mock user", getText("zk_comp_4")
        }
    }

}
