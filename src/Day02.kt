class RockPaperScissors1 : StringAcceptor {
    var result = 0
        private set

    override fun applyString(input: String): Boolean {
        val play = input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val p1 = play[0][0].code - 'A'.code
        val p2 = play[1][0].code - 'X'.code
        result = if (p1 == p2) {
            p2 + 4
        } else if (
            (p2 == 0 && p1 == 2)
            || (p2 == 1 && p1 == 0)
            || (p2 == 2 && p1 == 1)
        ) {
            p2 + 7
        } else {
            p2 + 1
        }
        return true
    }
}

class RockPaperScissors2 : StringAcceptor {
    var result = 0
        private set

    override fun applyString(input: String): Boolean {
        val play = input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val p1 = play[0][0].code - 'A'.code
        val playResult = play[1][0].code - 'X'.code
        result = when (playResult) {
            0 -> getLose(p1) + 1
            1 -> p1 + 4
            2 -> getWin(p1) + 7
            else -> throw RuntimeException()
        }
        return true
    }

    companion object {
        private fun getWin(p: Int): Int {
            val win = p + 1
            return if (win > 2) 0 else win
        }

        private fun getLose(p: Int): Int {
            val lose = p - 1
            return if (lose < 0) 2 else lose
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        return transform(input) { RockPaperScissors1() }.map(RockPaperScissors1::result).sum()
    }

    fun part2(input: List<String>): Int {
        return transform(input) { RockPaperScissors2() }.map(RockPaperScissors2::result).sum()
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}