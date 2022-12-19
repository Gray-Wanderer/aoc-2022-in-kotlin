class CalorieCounting : StringAcceptor {
    var sum = 0
    override fun applyString(input: String): Boolean {
        return if (input.isNotBlank()) {
            sum += input.toInt()
            false
        } else {
            true
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        return transform(input) { CalorieCounting() }.maxOfOrNull(CalorieCounting::sum) ?: 0
    }

    fun part2(input: List<String>): Int {
        val sums = transform(input) { CalorieCounting() }.map(CalorieCounting::sum).sortedDescending()
        return sums[0] + sums[1] + sums[2]
    }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
