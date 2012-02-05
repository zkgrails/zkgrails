package zk.hangman

import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.grails.composer.GrailsComposer

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

        viewModel.hangman = new Hangman()
        viewModel.hangman.init(['ABC','DEF','GHI'])

    }

    // @Listen(['onClick = #buttonRow1 > button','onClick = #buttonRow2 > button'])
    def guess(ch) {
        viewModel.hangman.guess(ch[0] as char)
    }

    @Listen('onClick = #btnNewGame') newGame() {
        Executions.sendRedirect 'index.zul'
    }

}
