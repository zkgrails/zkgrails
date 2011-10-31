package zk

import org.zkoss.zk.grails.*
import org.zkoss.zk.grails.databind.*

class UserViewModel extends GrailsViewModel {

    List<User> userList
    User user = new User(name:"test", lastName:"last")

    // allow you to bind UI elements locally
    static binding = {
        txtName     value: "user.name", style:"nameIsLowerCase"
        txtLastName value: "user.lastName"
        txtFullName value: "fullname"
    }

    @DependsOn(['user.name', 'user.lastName'])
    def fullname = [
        forward: { "${user.name} ${user.lastName}" },
        reverse: {
            def (name, lastName) = it.split(' ')
            user.name = name
            user.lastName = lastName
        }
    ]

    @DependsOn('user.name')
    def nameIsLowerCase = {
        if ((user.name[0] as Character).isLowerCase())
            "color: red"
        else
            "color: black"
    }

}