package zk


import org.zkoss.zk.grails.composer.GrailsComposer;

class UserComposer extends GrailsComposer {

    def afterCompose = { wnd ->
        $('#buttonHolder').append {
            button(label:"B1.1")
            button(label:"B1.2")
            button(label:"B1.3")
        }
        viewModel.user = new User(name:"test", lastName: "last")
        binds wnd
    }

    void test(e) {
        alert("hello ${e.target.label}")
    }
}
