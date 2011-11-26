package zk

class UserViewModel {

    List<User> userList
    User user

    static binding = {
        txtName     value: "user.name", style: "colorForName"
        txtLastName value: "user.lastName"
        txtFullName value: "fullname"
    }

    def fullname = {
        get { "${user.name} ${user.lastName}" }
        set {
            def (name, lastName) = it.split(' ')
            user.name = name
            user.lastName = lastName
        }
    }

    def colorForName = {
        get {
            if (user.name.charAt(0).isLowerCase())
                "color: red"
            else
                "color: black"
        }
    }

}