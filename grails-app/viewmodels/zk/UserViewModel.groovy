package zk

import org.zkoss.zk.grails.*

class UserViewModel extends GrailsViewModel {

    List<User> userList
    User user = new User(name:"test", lastName:"last")

    static binding = {
        //
        // 'button > *' autowire: byName, property: 'value'
        //
        txtName     value: "user.name", style:"nameIsLowerCase"
        txtLastName value: "user.lastName"
        txtFullName value: "fullname"
    }


    //
    // psuedo property
    //
    def fullname = [
        forward: { "${user.name} ${user.lastName}" },
        reverse: {
            def (name, lastName) = it.split(' ')
            user.name = name
            user.lastName = lastName
        }
    ]

    def nameIsLowerCase = {
        if ((user.name[0] as Character).isLowerCase())
            "color: red"
        else
            "color: black"
    }

}