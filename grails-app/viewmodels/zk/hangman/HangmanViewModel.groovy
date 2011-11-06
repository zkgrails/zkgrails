package zk.hangman

import org.zkoss.zk.grails.GrailsViewModel
import org.zkoss.zk.grails.databind.DependsOn

class HangmanViewModel extends GrailsViewModel {

    Hangman hangman

    static binding = {
        lblCount    value:"count", style:"count"
        imgStep     src:"step"
        lblAnswer   value:"revealedWord"
    }

    @DependsOn(['hangman.win','hangman.gameOver','hangman.wrong'])
    def count = {
        if(hangman.win)
            [value:"you win !!", style:"color: green"]
        else if(hangman.gameOver)
            [value:"you win !!", style:"color: red"]
        else
            [value:hangman.wrong, style:"color: black"]
    }

    @DependsOn('hangman.wrong')
    def step = { "images/hangman/step${hangman.wrong}.gif" }

    @DependsOn('hangman.wrong')
    def revealedWord = {
        def r=''
        hangman.buffer.each {
          r += "${it} "
        }
        return r
    }


}