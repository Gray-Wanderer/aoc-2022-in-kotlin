class TuningTrouble(private val strLen: Int) : StringAcceptor {
    var startSeq = 0
        private set

    override fun applyString(input: String): Boolean {
        for (i in strLen until input.length) {
            if (isUniqCars(input.substring(i - strLen, i))) {
                startSeq = i
                break
            }
        }
        return true
    }

    private fun isUniqCars(s: String): Boolean {
        val chars = mutableSetOf<Char>()
        for (c in s.toCharArray()) {
            chars.add(c)
        }
        return chars.size == s.length
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return transform(input) { TuningTrouble(4) }.map(TuningTrouble::startSeq)[0]
    }

    fun part2(input: List<String>): Int {
        return transform(input) { TuningTrouble(14) }.map(TuningTrouble::startSeq)[0]
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 19)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}