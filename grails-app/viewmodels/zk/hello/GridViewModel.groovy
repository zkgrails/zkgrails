package zk.hello

class GridViewModel {

    List<User> users

    static binding = {
        gdMain (model: 'users') {

        }
    }

}
