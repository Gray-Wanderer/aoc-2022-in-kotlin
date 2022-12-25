import kotlin.math.abs


const val ROPE_LEN = 10
class RopeBridge(input: List<String>) {
    private val firstKnotPositions: MutableSet<Point?> = HashSet()
    private val lastKnotPositions: MutableSet<Point?> = HashSet()
    private val knots = arrayOfNulls<Point>(ROPE_LEN)

    init {
        for (i in 0 until ROPE_LEN) {
            knots[i] = Point(0, 0)
        }
        firstKnotPositions.add(knots[1])
        for (moveStr in input) {
            val move = moveStr.split(" ").dropLastWhile { it.isEmpty() }.toTypedArray()
            val count = Integer.valueOf(move[1])
            for (i in 0 until count) {
                moveHead(move[0])
                moveTails()
            }
        }
    }

    private fun moveHead(direction: String) {
        when (direction) {
            "R" -> knots[0] = Point(knots[0]!!.x + 1, knots[0]!!.y)
            "L" -> knots[0] = Point(knots[0]!!.x - 1, knots[0]!!.y)
            "U" -> knots[0] = Point(knots[0]!!.x, knots[0]!!.y + 1)
            "D" -> knots[0] = Point(knots[0]!!.x, knots[0]!!.y - 1)
            else -> throw RuntimeException()
        }
    }

    private fun moveTails() {
        for (i in 1 until knots.size) {
            knots[i] = moveTail(knots[i], knots[i - 1])
            if (i == 1) {
                firstKnotPositions.add(knots[i])
            }
        }
        lastKnotPositions.add(knots[ROPE_LEN - 1])
    }

    private fun moveTail(tail: Point?, head: Point?): Point {
        if (abs(head!!.x - tail!!.x) <= 1 && abs(head.y - tail.y) <= 1) {
            return tail
        }
        var newX = tail.x
        if (head.x != tail.x) {
            newX += if (tail.x < head.x) 1 else -1
        }
        var newY = tail.y
        if (head.y != tail.y) {
            newY += if (tail.y < head.y) 1 else -1
        }
        return Point(newX, newY)
    }

    val firstKnotPositionsCount: Int
        get() = firstKnotPositions.size
    val lastKnotPositionsCount: Int
        get() = lastKnotPositions.size
}

fun main() {
    fun part1(input: List<String>): Int {
        return RopeBridge(input).firstKnotPositionsCount
    }
    fun part2(input: List<String>): Int {
        return RopeBridge(input).lastKnotPositionsCount
    }

    check(part1(readInput("Day09_test")) == 13)
    check(part2(readInput("Day09_test2")) == 36)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}