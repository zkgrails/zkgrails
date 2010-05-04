package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class NonPackageComposerTests extends GroovyTestCase {

    void testNonPackageComposer() {
        selenium.with {
            setSpeed "250"
            open "/zk/non_package_composer_tests.zul"
            click "zk_comp_3"
            assertEquals "clicked", getText("zk_comp_3")
        }
    }


}
