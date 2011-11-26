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

    def step = {
        get {
            "images/hangman/step${hangman.wrong}.gif"
        }
    }

    def revealedWord = {
        get {
            def r=''
            hangman.buffer.each {
                r += "${it} "
            }
            return r
        }
    }

}