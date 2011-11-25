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
        // lblError visible: "user.hasErrors()" value:"user.errors.allErrors[0]"
        //
        // validate = false; default is true
    }

    @DependsOn(['user.name', 'user.lastName']) fullname = {
        get { "${user.name} ${user.lastName}" }
        set {
            def (name, lastName) = it.split(' ')
            user.name = name
            user.lastName = lastName
        }
    }

    @DependsOn('user.name') nameIsLowerCase = {
        get {
            if (user.name.charAt(0).isLowerCase())
                "color: red"
            else
                "color: black"
        }
    }
}