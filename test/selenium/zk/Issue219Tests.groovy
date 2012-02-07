package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue219Tests extends GroovyTestCase {

    void testURLMapping() {
        selenium.with {
            setSpeed "1000"
            open "/zk/hello"
            // user label
            assertEquals "Hello", getText("zk_comp_3")
            click "zk_comp_3"
            assertEquals "Hello World", getText("zk_comp_2")
        }
    }

}
