package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class HelloViewModelTests extends GroovyTestCase {

    void testHelloViewModel() {
        selenium.with {
            setSpeed "1000"
            open "/zk/hello_vm"
            click "zk_comp_3"
            assertEquals "hello", getText("zk_comp_2")
        }
    }


}
