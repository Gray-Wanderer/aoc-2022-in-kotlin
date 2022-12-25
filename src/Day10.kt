
import java.util.function.Consumer


class CathodeRayTube(input: List<String>) {
    private var states: MutableList<Int> = ArrayList()
    private var x = 1

    init {
        input.forEach(Consumer { comand: String -> executeComand(comand) })
    }

    private fun executeComand(comand: String) {
        if (comand == "noop") {
            states.add(x)
        } else {
            val parts = comand.split(" ").dropLastWhile { it.isEmpty() }.toTypedArray()
            states.add(x)
            states.add(x)
            x += Integer.valueOf(parts[1])
        }
    }

    val signalPowerSum: Int
        get() {
            var acc = 0
            var i = 19
            while (i < states.size) {
                acc += states[i] * (i + 1)
                i += 40
            }
            return acc
        }

    fun drawSignal() {
        var tick = 0
        for (i in 0..5) {
            for (j in 0..39) {
                print(if (inSprite(tick, j + 1)) "\u2588" else " ")
                tick++
            }
            kotlin.io.println()
        }
    }

    private fun inSprite(tick: Int, pos: Int): Boolean {
        val x = states[tick]
        return x <= pos && pos <= x + 2
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return CathodeRayTube(input).signalPowerSum
    }
    fun part2(input: List<String>) {
        return CathodeRayTube(input).drawSignal()
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    part2(testInput)

    val input = readInput("Day10")
    part1(input).println()
    part2(input)
}