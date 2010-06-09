package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue96Tests extends GroovyTestCase {

    void testZulTagWithBorderLayoutAsFirstNode() {
        selenium.with {
            setSpeed "1000"
            open "/zk/issue96"
            // z-borderlayout
            assertEquals "z-borderlayout", getAttribute("zk_comp_1@class")
        }
    }

}
