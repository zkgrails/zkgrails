package zk

import grails.test.*
import com.thoughtworks.selenium.*
import grails.plugins.selenium.SeleniumAware

@Mixin(SeleniumAware)
class Issue131Tests extends GroovyTestCase {

    void testMessage() {
        selenium.with {
            setSpeed "1000"
            open "/zk/issue_131.zul"
/*            
            assertEquals "ทดสอบข้อความ", getText("zk_comp_2")
            click "zk_comp_2"
            assertEquals "คลิ๊ก", getText("zk_comp_2")
            click "zk_comp_3"
            assertEquals "คลิ๊ก test", getText("zk_comp_3")
*/
        }
    }

}
