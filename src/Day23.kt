import kotlin.math.max
import kotlin.math.min

val NORTH_MOVE = object : Mover() {
    override fun check(positionByElves: Map<Point, Elf>, p: Point): Boolean {
        return !positionByElves.containsKey(Point(p.x - 1, p.y - 1))
                && !positionByElves.containsKey(Point(p.x, p.y - 1))
                && !positionByElves.containsKey(Point(p.x + 1, p.y - 1))
    }

    override fun move(p: Point): Point {
        return Point(p.x, p.y - 1)
    }
}
val SOUTH_MOVE = object : Mover() {
    override fun check(positionByElves: Map<Point, Elf>, p: Point): Boolean {
        return !positionByElves.containsKey(Point(p.x - 1, p.y + 1))
                && !positionByElves.containsKey(Point(p.x, p.y + 1))
                && !positionByElves.containsKey(Point(p.x + 1, p.y + 1))
    }

    override fun move(p: Point): Point {
        return Point(p.x, p.y + 1)
    }
}
val WEST_MOVE = object : Mover() {
    override fun check(positionByElves: Map<Point, Elf>, p: Point): Boolean {
        return !positionByElves.containsKey(Point(p.x - 1, p.y + 1))
                && !positionByElves.containsKey(Point(p.x - 1, p.y))
                && !positionByElves.containsKey(Point(p.x - 1, p.y - 1))
    }

    override fun move(p: Point): Point {
        return Point(p.x - 1, p.y)
    }
}
val EAST_MOVE = object : Mover() {
    override fun check(positionByElves: Map<Point, Elf>, p: Point): Boolean {
        return !positionByElves.containsKey(Point(p.x + 1, p.y + 1))
                && !positionByElves.containsKey(Point(p.x + 1, p.y))
                && !positionByElves.containsKey(Point(p.x + 1, p.y - 1))
    }

    override fun move(p: Point): Point {
        return Point(p.x + 1, p.y)
    }
}

val isElvesAround: (Map<Point, Elf>, Point) -> Boolean = a@{ positionByElves, p ->
    for (i in -1..1) {
        for (j in -1..1) {
            if (i != 0 || j != 0) {
                if (positionByElves.containsKey(Point(p.x + i, p.y + j))) {
                    return@a true
                }
            }
        }
    }
    return@a false
}


abstract class Mover {
    abstract fun check(positionByElves: Map<Point, Elf>, p: Point): Boolean
    abstract fun move(p: Point): Point
}

class Elf(var position: Point) {
    private val directions: MutableList<Mover> = mutableListOf(
        NORTH_MOVE, SOUTH_MOVE, WEST_MOVE, EAST_MOVE
    )
    private var nextDirection: Mover? = null
    var nextPosition: Point = position

    fun prepareToMove(elvesPositions: Map<Point, Elf>) {
        if (isElvesAround(elvesPositions, position)) {
            nextDirection = directions.find { it.check(elvesPositions, position) }
        }
        nextPosition = nextDirection?.move(position) ?: position
    }

    fun move(): Boolean {
        directions.add(directions.removeFirst())

        return if (nextDirection != null) {
            position = nextPosition
            nextDirection = null
            true
        } else false
    }

    fun cancelMove() {
        nextPosition = position
        nextDirection = null
    }
}

fun main() {
    fun solution(input: List<String>, iterationsCount: Int? = null): Point {
        var elvesPositions: MutableMap<Point, Elf> = mutableMapOf()
        val elves = mutableListOf<Elf>()
        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                if (c == '#') {
                    val point = Point(x, y)
                    val elf = Elf(point)
                    elvesPositions[point] = elf
                    elves.add(elf)
                }
            }
        }

        var step = 0
        var hasChanges = true
        while (hasChanges && (iterationsCount == null || step < iterationsCount)) {
            step++
            hasChanges = false

            val newElvesPositions = mutableMapOf<Point, Elf>()
            elves.forEach {
                it.prepareToMove(elvesPositions)
                if (newElvesPositions.containsKey(it.nextPosition)) {
                    newElvesPositions[it.nextPosition]?.cancelMove()
                    it.cancelMove()
                } else {
                    newElvesPositions[it.nextPosition] = it
                }
            }
            elvesPositions = mutableMapOf()
            elves.forEach {
                hasChanges = it.move() || hasChanges
                elvesPositions[it.position] = it
            }
        }

        var minX = elves[0].position.x
        var maxX = elves[0].position.x
        var minY = elves[0].position.y
        var maxY = elves[0].position.y
        elves.forEach {
            minX = min(minX, it.position.x)
            maxX = max(maxX, it.position.x)
            minY = min(minY, it.position.y)
            maxY = max(maxY, it.position.y)
        }
        return Point((maxX - minX + 1) * (maxY - minY + 1) - elves.size, step)
    }

    val testInput1 = readInput("Day23_test1")
    check(solution(testInput1, 3).x == 25)
    val testInput2 = readInput("Day23_test2")
    check(solution(testInput2, 10).x == 110)
    check(solution(testInput2).y == 20)

    val input = readInput("Day23")
    solution(input, 10).x.println()
    solution(input).y.println()
}