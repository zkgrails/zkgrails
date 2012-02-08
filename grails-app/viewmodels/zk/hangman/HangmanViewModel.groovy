package zk.hangman

class HangmanViewModel {

    Hangman hangman

    static binding = {
        lblCount  value:"count", style:"count"
        imgStep   src:"step"
        lblAnswer value:"revealedWord"
    }

    def count = {
        get {
            if(hangman.win)
                [value: "you win !!",  style: "color: green"]
            else if(hangman.gameOver)
                [value: "you lose !!", style: "color: red"  ]
            else
                [value: hangman.wrong, style: "color: black"]
        }
    }

    String getStep() {
        "images/hangman/step${hangman.wrong}.gif"
    }

    String getRevealedWord() {
        def r=''
        hangman.buffer.each {
            r += "${it} "
        }
        return r
    }

}