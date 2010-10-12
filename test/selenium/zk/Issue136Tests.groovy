package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue136Tests extends GroovyTestCase {

    void testWrapHTML() {
        selenium.with {
            setSpeed("1000")
            open "/zk/issue136"
            assertEquals "ciao", getText("zk_comp_4").trim()
        }
    }

}
