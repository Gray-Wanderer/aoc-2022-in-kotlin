class CampCleanup : StringAcceptor {
    private var p1: Pair? = null
    private var p2: Pair? = null
    var isInclude = false
        private set
    var isOverlaps = false
        private set

    override fun applyString(input: String): Boolean {
        val ranges = input.split(",").dropLastWhile { it.isEmpty() }.toTypedArray()
        p1 = Pair(ranges[0])
        p2 = Pair(ranges[1])
        isInclude = Pair.include(p1!!, p2!!)
        isOverlaps = Pair.overlaps(p1!!, p2!!)
        return true
    }

    private class Pair(s: String) {
        var start: Int
        var end: Int

        init {
            val ranges = s.split("-").dropLastWhile { it.isEmpty() }.toTypedArray()
            start = Integer.valueOf(ranges[0])
            end = Integer.valueOf(ranges[1])
        }

        companion object {
            fun include(p1: Pair, p2: Pair): Boolean {
                return p1.start >= p2.start && p1.end <= p2.end || p2.start >= p1.start && p2.end <= p1.end
            }

            fun overlaps(p1: Pair, p2: Pair): Boolean {
                return p1.start.coerceAtLeast(p2.start) <= p1.end.coerceAtMost(p2.end)
            }
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return transform(input) { CampCleanup() }.count(CampCleanup::isInclude)
    }

    fun part2(input: List<String>): Int {
        return transform(input) { CampCleanup() }.count(CampCleanup::isOverlaps)
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}