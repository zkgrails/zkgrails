package zk

import org.zkoss.zk.grails.*

class UserViewModel extends GrailsViewModel {

    List<User> userList
    User user


    static binding = {
        'button > *' autowire: byName, property: 'value'
    }


    //
    // psuedo property
    //
    def fullname = [
        forward: { "${user.name} ${user.lastName}" },
        reverse: { (user.name, user.lastName) = it.split(' ') }
    ]

    def nameIsLowerCase = {
        if (user.name[0].isLowerCase())
            "red"
        else
            "black"
    }

}