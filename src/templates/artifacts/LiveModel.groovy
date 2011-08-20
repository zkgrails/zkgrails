@artifact.package@import org.zkoss.zk.grails.*

class @artifact.name@  {

    static config = {
        model    "page" // or "list"
        domain   @artifact.domain@.class
        pageSize 20
        sorted   true
    }

}
