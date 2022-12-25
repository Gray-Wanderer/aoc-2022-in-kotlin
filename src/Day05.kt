class SupplyStacks(text: List<String>, private var invertOnMove: Boolean) : StringAcceptor {
    private var text = text.toMutableList()

    override fun applyString(input: String): Boolean {
        //"move 1 from 2 to 1"
        val splits = input.split(" ").dropLastWhile { it.isEmpty() }.toTypedArray()
        val count = Integer.valueOf(splits[1])
        val from = Integer.valueOf(splits[3]) - 1
        val to = Integer.valueOf(splits[5]) - 1
        move(count, from, to)
        return false
    }

    private fun move(count: Int, from: Int, to: Int) {
        val substFrom = StringBuilder(text[from].substring(0, count))
        if (invertOnMove) {
            substFrom.reverse()
        }
        text[from] = text[from].substring(count)
        text[to] = substFrom.append(text[to]).toString()
    }

    val head: String
        get() {
            val head = StringBuilder()
            for (t in text) {
                head.append(t[0])
            }
            return head.toString()
        }
}

fun parseState(input: List<String>): List<String> {
    val builders = mutableListOf<StringBuilder>()
    var chars = input[0].toCharArray()
    var j = 1
    while (j < chars.size) {
        val sb = StringBuilder()
        if (' ' != chars[j]) {
            sb.append(chars[j])
        }
        builders.add(sb)
        j += 4
    }
    for (i in 1 until input.size) {
        chars = input[i].toCharArray()
        j = 1
        var k = 0
        while (j < chars.size) {
            val sb = builders[k]
            if (' ' != chars[j]) {
                sb.append(chars[j])
            }
            j += 4
            k++
        }
    }
    return builders.map(StringBuilder::toString)
}

fun main() {
    fun solve(input: String, invertOnMove: Boolean): String {
        val parts = input.split("\n\n")
        val state = parts[0].split("\n")
        val instructions = parts[1].split("\n")

        return transform(instructions) {
            SupplyStacks(
                parseState(state.subList(0, state.size - 1)),
                invertOnMove
            )
        }[0].head
    }

    val testInput = readInputAsText("Day05_test")
    check(solve(testInput, true) == "CMZ")
    check(solve(testInput, false) == "MCD")

    val input = readInputAsText("Day05")
    solve(input, true).println()
    solve(input, false).println()
}