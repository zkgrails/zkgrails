package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class SimpleWebTests extends GroovyTestCase {

    void testNonPackageComposer() {
        selenium.with {
            setSpeed "250"
            open "/zk/non_package_composer_tests.zul"
            click "zk_comp_3"
            assertEquals "clicked", getText("zk_comp_3")
        }
    }

    void testPackageComposer() {
        selenium.with {
            setSpeed("500")
            open("/zk/test/index.zul")
            focus("zk_comp_9")
            type("zk_comp_9", "test")
            fireEvent("zk_comp_9", "blur")
            assertEquals("test", getAttribute("zk_comp_9@value"))

            focus("zk_comp_10")
            click("zk_comp_10")
            assertEquals("done", getAttribute("zk_comp_9@value"))

            click("zk_comp_14-real")
            assertEquals("strike z-checkbox", getAttribute("zk_comp_14@class"))
            click("zk_comp_14-real")
            assertEquals("z-checkbox", getAttribute("zk_comp_14@class"))
        }
    }

}
