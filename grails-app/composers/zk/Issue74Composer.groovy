package zk

import org.zkoss.zkgrails.*

class Issue74Composer extends org.zkoss.zk.grails.composer.GrailsComposer {
    
    def lblUser
    def lblUser_2

    def afterCompose = { window ->
        session['user'] = "mock user"
        lblUser.value = session['user']
        session.user2 = "mock user 2"
        lblUser_2.value = session.user2
    }

}
