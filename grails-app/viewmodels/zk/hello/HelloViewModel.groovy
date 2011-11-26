package zk.hello

import org.zkoss.zk.grails.GrailsViewModel

class HelloViewModel extends GrailsViewModel {

    static binding = {
        lblMessage value:"message"
    }

    String message

}
