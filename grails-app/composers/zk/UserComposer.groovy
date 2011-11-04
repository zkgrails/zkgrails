package zk

import org.zkoss.zk.grails.GrailsComposer

import org.zkoss.zk.grails.Listen

class UserComposer extends GrailsComposer {

    def afterCompose = { wnd ->
        $('#buttonHolder').append {
            button(label:"B1.1")
            button(label:"B1.2")
            button(label:"B1.3")
        }

        binds wnd
    }

    @Listen('#buttonHolder > button.onClick') test(e) {
        alert("hello ${e.target.label}")
    }
}
