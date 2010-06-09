package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue78Tests extends GroovyTestCase {

    void testZulResponseWrapper() {
        selenium.with {
            setSpeed "1000"
            // a controller: TestController.index
            // with views/test/index.gsp
            // and z:head, z:body
            open "/zk/test"
            // image
            assertEquals "/zk/test/../images/grails_logo.png", getAttribute("zk_comp_4@src")
            // button
            assertEquals "add", getText("zk_comp_10")
        }
    }

}
