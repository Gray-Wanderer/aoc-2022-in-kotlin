fun interface BlizzardMover {
    fun move(p: Point, limit: Point): Point
}

val UP_B_MOVE = BlizzardMover { p, limit ->
    if (p.y == 1) Point(p.x, limit.y - 1)
    else Point(p.x, p.y - 1)
}

val DOWN_B_MOVE = BlizzardMover { p, limit ->
    if (p.y == limit.y - 1) Point(p.x, 1)
    else Point(p.x, p.y + 1)
}

val LEFT_B_MOVE = BlizzardMover { p, limit ->
    if (p.x == 1) Point(limit.x - 1, p.y)
    else Point(p.x - 1, p.y)
}

val RIGHT_B_MOVE = BlizzardMover { p, limit ->
    if (p.x == limit.x - 1) Point(1, p.y)
    else Point(p.x + 1, p.y)
}

class Blizzard(var point: Point, private val mover: BlizzardMover, private val limit: Point) {
    fun move() {
        point = mover.move(point, limit)
    }
}

class BlizzardRouteSearcher(input: List<String>) {
    private val limit: Point
    private val blizzards: MutableList<Blizzard> = mutableListOf()
    private var startPoint = Point(1,0)
    private var endPoint: Point
    init {
        limit = Point(input[0].length - 1, input.size - 1)
        endPoint = Point(input[0].length - 2, input.size - 1)

        input.forEachIndexed{y,row ->
            row.forEachIndexed { x, c ->
                when (c) {
                    '>' -> blizzards.add(Blizzard(Point(x,y),RIGHT_B_MOVE,limit))
                    'v' -> blizzards.add(Blizzard(Point(x,y),DOWN_B_MOVE,limit))
                    '<' -> blizzards.add(Blizzard(Point(x,y),LEFT_B_MOVE,limit))
                    '^' -> blizzards.add(Blizzard(Point(x,y),UP_B_MOVE,limit))
                }
            }
        }
    }

    fun countRouteSteps(): Int {
        var i = 0
        var currentStepPositions = mutableSetOf(startPoint)
        while (true) {
            i++
            moveBlizzards(currentStepPositions)
            val nextStepPositions = mutableSetOf<Point>()
            currentStepPositions.forEach {
                nextStepPositions.add(it)
                if (it.y > 0 && it.y < limit.y) {
                    if (it.x > 1) {
                        nextStepPositions.add(Point(it.x - 1, it.y))
                    }
                    if (it.x < limit.x - 1) {
                        nextStepPositions.add(Point(it.x + 1, it.y))
                    }
                }
                if (it.y == endPoint.y - 1 && it.x == endPoint.x) {
                    moveBlizzards(currentStepPositions)
                    return i+1
                } else if (it.y < limit.y-1) {
                    nextStepPositions.add(Point(it.x, it.y + 1))
                }
                if (it.y == endPoint.y + 1 && it.x == endPoint.x) {
                    moveBlizzards(currentStepPositions)
                    return i+1
                } else if (it.y > 1) {
                    nextStepPositions.add(Point(it.x, it.y - 1))
                }
            }
            currentStepPositions = nextStepPositions
        }
    }

    fun switchStartAndFinish() {
        endPoint = startPoint.also { startPoint = endPoint }
    }

    private fun moveBlizzards(currentStepPositions: MutableSet<Point>) {
        blizzards.forEach{
            it.move()
            currentStepPositions.remove(it.point)
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return BlizzardRouteSearcher(input).countRouteSteps()
    }

    fun part2(input: List<String>): Int {
        val routeSearcher = BlizzardRouteSearcher(input)
        var result = routeSearcher.countRouteSteps()
        routeSearcher.switchStartAndFinish()
        result += routeSearcher.countRouteSteps()
        routeSearcher.switchStartAndFinish()
        result += routeSearcher.countRouteSteps()
        return result
    }

    val testInput = readInput("Day24_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 54)

    val input = readInput("Day24")
    part1(input).println()
    part2(input).println()
}