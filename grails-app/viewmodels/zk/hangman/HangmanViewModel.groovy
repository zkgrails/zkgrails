package zk.hangman

import org.zkoss.zk.grails.GrailsViewModel

class HangmanViewModel extends GrailsViewModel {

    Hangman hangman

    static binding = {
        count    value:"count", style:"count"
        imgStep  src:"step"
        answer   value:"hangman.revealedWord"
    }

    def count = {
        if(hangman.win)
            [value:"you win !!", style:"color: green"]
        else if(hangman.gameOver) {
            [value:"you win !!", style:"color: red"]
        } else {
            [value:hangman.wrong, style:"color: black"]
        }
    }

    def step = { "images/hangman/step${hangman.wrong}.gif" }
}