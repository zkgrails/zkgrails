package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue119Tests extends GroovyTestCase {

    void test_taglib_Z_resource() {
        selenium.with {
            setSpeed "1000"
            open "/zk/issue_119.zul"
            def src = getAttribute("zk_comp_1@src")
            assertTrue src.startsWith("/zk/static/images/grails_logo.png")
        }
    }

}
