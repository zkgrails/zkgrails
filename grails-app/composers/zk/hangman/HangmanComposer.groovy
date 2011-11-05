package zk.hangman

import org.zkoss.zk.grails.GrailsComposer
import org.zkoss.zk.grails.Listen
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.grails.databind.Attr

class HangmanComposer extends GrailsComposer {

    def buttonRow1
    def buttonRow2

    def afterCompose = { wnd ->
        buttonRow1.append {
            ('A'..'M').each { ch ->
                button(label: ch, width: "28px")
            }
        }
        buttonRow2.append {
            ('N'..'Z').each { ch ->
                button(label: ch, width: "28px")
            }
        }

        // binds wnd
    }

    @Listen(['#buttonRow1 > button.onClick','#buttonRow2 > button.onClick'])
    def guess(@Attr('label') ch) {
        alert(ch)
        viewModel.hangman.guess(ch)
    }

    @Listen('#btnNewGame.onClick') newGame() {
        Executions.sendRedirect 'index.zul'
    }
}