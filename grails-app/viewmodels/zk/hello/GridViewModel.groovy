package zk.hello

import zk.User

class GridViewModel {

    List<User> users

    static binding = {
        gdMain (model: 'users') {

        }
    }

}
