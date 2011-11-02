package zk.hangman

import org.zkoss.zk.grails.GrailsComposer
import org.zkoss.zk.grails.Handler

class HangmanComposer extends GrailsComposer {

    def buttonRow1
    def buttonRow2

    def afterCompose = { wnd ->
        for(ch1 in ['A'..'M']) {
            buttonRow1.append { button(label: ch1, width: "28px") }
        }
        for(ch2 in ['N'..'Z']) {
            buttonRow1.append { button(label: ch2, width: "28px") }
        }

        viewModel?.binds wnd
    }

    @Handler(['#buttonRow1 > button.onClick','#buttonRow1 > button.onClick'])
    def guess(e) {

    }
}