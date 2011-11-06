package zk.hangman

class Hangman {

    String  word
    Boolean win = false
    Boolean gameOver = false
    char[]  buffer
    Integer correct = 0
    Integer wrong = 0

    def init(words) {
        word = words[new Random().nextInt(3)]
        buffer = new char[word.size()]
        word.eachWithIndex {obj, i ->
          this.buffer[i] = '_'
        }
        wrong = 0
        gameOver = false
        correct = 0
    }

    def guess(char ch) {
        if (win || gameOver) return
        def right = false
        word.eachWithIndex { it, i ->
            if (it == ch) {
                buffer[i] = it
                correct++
                right = true
            }
        }
        if (!right) {
            wrong++
            if (wrong == 12) gameOver = true
        } else {
            if (correct == word.length()) win = true
        }
    }

}
