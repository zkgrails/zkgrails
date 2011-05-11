package zk

import org.zkoss.zkgrails.*

class Issue74Composer extends GrailsComposer {
    
    def lblUser

    def afterCompose = { window ->
        session['user'] = "mock user"
        lblUser.value = session['user']
    }

}
