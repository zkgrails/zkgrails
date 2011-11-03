package zk

import org.zkoss.zk.grails.GrailsComposer

import org.zkoss.zk.grails.Listen

class UserComposer extends GrailsComposer {
    def buttonHolder

    def afterCompose = { wnd ->
        buttonHolder.append {
            button(label:"B1")
            button(label:"B2")
            button(label:"B3")
        }

        viewModel?.binds wnd
    }

    @Listen('#buttonHolder > button.onClick')
    def test(e) {
        alert("hello ${e.target.label}")
    }
}
