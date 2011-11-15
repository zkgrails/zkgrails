package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class HashTagTests extends GroovyTestCase {

    void testActionViaTag() {
        selenium.with {
            setSpeed "1000"
            open "/zk/hello#world"
            assertEquals "Hello World via Tag", getText("zk_comp_2")
        }
    }


}
