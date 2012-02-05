package zk

import org.zkoss.zkgrails.*

class Issue47Composer extends org.zkoss.zk.grails.composer.GrailsComposer {
    
    static scaffold = true

    def afterCompose = { window ->
        if(User.count() == 0) {
            100.times {
                new User(name: "test-${it+1}").save()
            }
        }
        if(Comment.count() == 0) {
            100.times {
                new Comment(content:"comment-${it+1}").save()
            }
        }
    }
}