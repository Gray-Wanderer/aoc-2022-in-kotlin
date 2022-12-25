import java.lang.RuntimeException

class SnafuSum(input: List<String>) {
    private val snafuSum = mutableListOf<Int>()

    init {
        input.forEach{
            for (i in snafuSum.size until it.length) {
                snafuSum.add(0)
            }
            it.reversed().forEachIndexed { i, c ->
                when (c) {
                    '-' -> snafuSum[i] -= 1
                    '=' -> snafuSum[i] -= 2
                    else -> snafuSum[i] += (c - '0')
                }
            }
        }
        for(i in 0 until snafuSum.size) {
            var over = snafuSum[i] / 5
            var n = snafuSum[i] % 5
            if (n >= 3) {
                n -= 5
                over += 1
            } else if (n <= -3) {
                n += 5
                over -= 1
            }
            if (over != 0) {
                if (i == snafuSum.size - 1) {
                    snafuSum.add(0)
                }
                snafuSum[i] = n
                snafuSum[i+1] += over
            }
        }
    }

    fun getSnafuSum():String {
        var s = ""
        snafuSum.forEach {
            s += when (it) {
                0,1,2 -> it
                -1 -> '-'
                -2 -> '='
                else -> throw RuntimeException("Unexpected number: $it")
            }
        }
        return s.reversed()
    }
}

fun main() {
    fun part1(input: List<String>): String {
        return SnafuSum(input).getSnafuSum()
    }

    val testInput = readInput("Day25_test")
    check(part1(testInput) == "2=-1=0")

    val input = readInput("Day25")
    part1(input).println()
}