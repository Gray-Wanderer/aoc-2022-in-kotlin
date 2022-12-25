import java.lang.RuntimeException
import java.util.regex.Pattern

const val RIGHT = 0
const val DOWN = 1
const val LEFT = 2
const val UP = 3

class CubeIntList(
    val v: Int,
    var right: CubeIntList? = null,
    var rightDirection: Int = RIGHT,
    var down: CubeIntList? = null,
    var downDirection: Int = DOWN,
    var left: CubeIntList? = null,
    var leftDirection: Int = LEFT,
    var up: CubeIntList? = null,
    var upDirection: Int = UP
)

class Surface(mapInput: List<String>, x: Int, y: Int, surfaceSize: Int) {
    val surface: Array<Array<CubeIntList>>

    init {
        surface = Array(surfaceSize) { i ->
            Array(surfaceSize) { j ->
                CubeIntList(v = when (mapInput[i + x][j + y]) {
                    '.' -> 0
                    '#' -> 1
                    else -> -1
                })
            }
        }

        for (i in 0 until surfaceSize) {
            for (j in 0 until surfaceSize) {
                if (i > 0) {
                    surface[i][j].up = surface[i - 1][j]
                }
                if (i < surfaceSize - 1) {
                    surface[i][j].down = surface[i + 1][j]
                }
                if (j > 0) {
                    surface[i][j].left = surface[i][j - 1]
                }
                if (j < surfaceSize - 1) {
                    surface[i][j].right = surface[i][j + 1]
                }
            }
        }
    }

    fun getPosition(item: CubeIntList): Point? {
        surface.forEachIndexed { i, row ->
            row.forEachIndexed { j, s ->
                if (s === item) {
                    return Point(i, j)
                }
            }
        }
        return null
    }
}

abstract class SurfaceMap(mapInput: List<String>, val surfaceSize: Int) {
    val surfaceMap: Array<Array<Surface?>>
    val start: CubeIntList

    init {
        val h = mapInput.size / surfaceSize
        val w = mapInput.maxOf(String::length) / surfaceSize
        surfaceMap = Array(h) { i ->
            val x = i * surfaceSize
            Array(w) { j ->
                val y = j * surfaceSize
                if (y >= mapInput[x].length || mapInput[x][y] == ' ') {
                    null
                } else {
                    Surface(mapInput = mapInput, x = x, y = y, surfaceSize = surfaceSize)
                }
            }
        }
        start = surfaceMap[0].find { x -> x != null }!!.surface[0][0]

        this.connect()
    }

    abstract fun connect()

    fun getUpSurface(x: Int, y: Int): Surface {
        var i = x
        do {
            i -= 1
            if (i < 0) {
                i = surfaceMap.size - 1
            }
            if (surfaceMap[i][y] != null) {
                return surfaceMap[i][y]!!
            }
        } while (true)
    }

    fun getDownSurface(x: Int, y: Int): Surface {
        var i = x
        do {
            i += 1
            if (i >= surfaceMap.size) {
                i = 0
            }
            if (surfaceMap[i][y] != null) {
                return surfaceMap[i][y]!!
            }
        } while (true)
    }

    fun getRightSurface(x: Int, y: Int): Surface {
        var j = y
        do {
            j += 1
            if (j >= surfaceMap[x].size) {
                j = 0
            }
            if (surfaceMap[x][j] != null) {
                return surfaceMap[x][j]!!
            }
        } while (true)
    }

    fun getLeftSurface(x: Int, y: Int): Surface {
        var j = y
        do {
            j -= 1
            if (j < 0) {
                j = surfaceMap[x].size - 1
            }
            if (surfaceMap[x][j] != null) {
                return surfaceMap[x][j]!!
            }
        } while (true)
    }

    fun getPosition(item: CubeIntList): Point? {
        surfaceMap.forEachIndexed { i, row ->
            row.forEachIndexed { j, surface ->
                val p = surface?.getPosition(item)
                if (p != null) {
                    return Point(p.x + i * surfaceSize, p.y + j * surfaceSize)
                }
            }
        }
        return null
    }
}

class SurfaceFlatMap(mapInput: List<String>, surfaceSize: Int) : SurfaceMap(mapInput, surfaceSize) {

    override fun connect() {
        surfaceMap.forEachIndexed { i, row ->
            row.forEachIndexed { j, s ->
                if (s != null) {
                    val up = getUpSurface(i, j)
                    val down = getDownSurface(i, j)
                    val left = getLeftSurface(i, j)
                    val right = getRightSurface(i, j)
                    for (k in 0 until surfaceSize) {
                        s.surface[0][k].up = up.surface[surfaceSize - 1][k]
                        s.surface[k][surfaceSize - 1].right = right.surface[k][0]
                        s.surface[surfaceSize - 1][k].down = down.surface[0][k]
                        s.surface[k][0].left = left.surface[k][surfaceSize - 1]
                    }
                }
            }
        }
    }
}

class MyPuzzleSurfaceCubeMap(mapInput: List<String>, surfaceSize: Int) : SurfaceMap(mapInput, surfaceSize) {

    /*
     12
     3
    45
    6
     */
    override fun connect() {
        val s1 = surfaceMap[0][1]!!.surface
        val s2 = surfaceMap[0][2]!!.surface
        val s3 = surfaceMap[1][1]!!.surface
        val s4 = surfaceMap[2][0]!!.surface
        val s5 = surfaceMap[2][1]!!.surface
        val s6 = surfaceMap[3][0]!!.surface

        val lastInd = surfaceSize - 1
        for (k in 0 until surfaceSize) {
            s1[k][lastInd].right = s2[k][0]
            s1[lastInd][k].down = s3[0][k]
            s1[k][0].left = s4[lastInd - k][0]
            s1[k][0].leftDirection = RIGHT
            s1[0][k].up = s6[k][0]
            s1[0][k].upDirection = RIGHT

            s2[k][lastInd].right = s5[lastInd - k][lastInd]
            s2[k][lastInd].rightDirection = LEFT
            s2[lastInd][k].down = s3[k][lastInd]
            s2[lastInd][k].downDirection = LEFT
            s2[k][0].left = s1[k][lastInd]
            s2[0][k].up = s6[lastInd][k]

            s3[k][lastInd].right = s2[lastInd][k]
            s3[k][lastInd].rightDirection = UP
            s3[lastInd][k].down = s5[0][k]
            s3[k][0].left = s4[0][k]
            s3[k][0].leftDirection = DOWN
            s3[0][k].up = s1[lastInd][k]

            s4[k][lastInd].right = s5[k][0]
            s4[lastInd][k].down = s6[0][k]
            s4[k][0].left = s1[lastInd - k][0]
            s4[k][0].leftDirection = RIGHT
            s4[0][k].up = s3[k][0]
            s4[0][k].upDirection = RIGHT

            s5[k][lastInd].right = s2[lastInd - k][lastInd]
            s5[k][lastInd].rightDirection = LEFT
            s5[lastInd][k].down = s6[k][lastInd]
            s5[lastInd][k].downDirection = LEFT
            s5[k][0].left = s4[k][lastInd]
            s5[0][k].up = s3[lastInd][k]

            s6[k][lastInd].right = s5[lastInd][k]
            s6[k][lastInd].rightDirection = UP
            s6[lastInd][k].down = s2[0][k]
            s6[k][0].left = s1[0][k]
            s6[k][0].leftDirection = DOWN
            s6[0][k].up = s4[lastInd][k]
        }
    }
}

fun goByRoute(map: SurfaceMap, route: List<RoutePiece>): Int {
    var p = map.start
    var direction = 0

    for (routePart in route) {
        direction = direction.rotate(routePart.rotate)
        for (step in 1..routePart.steps) {
            val nextPoint = when (direction) {
                RIGHT -> p.right!!
                DOWN -> p.down!!
                LEFT -> p.left!!
                UP -> p.up!!
                else -> throw RuntimeException()
            }
            if (nextPoint.v == 0) {
                direction = when (direction) {
                    RIGHT -> p.rightDirection
                    DOWN -> p.downDirection
                    LEFT -> p.leftDirection
                    UP -> p.upDirection
                    else -> throw RuntimeException()
                }
                p = nextPoint
            }
        }
    }
    val mapPoint = map.getPosition(p)!!
    return (mapPoint.x + 1) * 1000 + (mapPoint.y + 1) * 4 + direction
}

class RoutePiece(val steps: Int, val rotate: Int)

fun parseRoure(input: String): List<RoutePiece> {
    val roure = mutableListOf<RoutePiece>()
    val matcher = Pattern.compile("(\\w)(\\d+)").matcher("N$input")
    while (matcher.find()) {
        roure.add(RoutePiece(
            steps = matcher.group(2).toInt(),
            rotate = when (matcher.group(0)[0]) {
                'R' -> 1
                'L' -> -1
                'N' -> 0
                else -> throw RuntimeException()
            }
        ))
    }
    return roure.toList()
}

fun Int.rotate(rotate: Int): Int {
    val newDirection = this + rotate
    return if (newDirection < 0) {
        4 + newDirection
    } else {
        newDirection % 4
    }
}

fun main() {
    fun part1(input: String, surfaceSize: Int, mapCreator: (mapInput: List<String>, surfaceSize: Int) -> SurfaceMap): Int {
        val inputParts = input.split("\n\n")
        val mapInput = inputParts[0].split("\n")
        val map = mapCreator(mapInput, surfaceSize)
        val route = parseRoure(inputParts[1])

        return goByRoute(map = map, route = route)
    }

    val testInput = readInputAsText("Day22_test")
    check(part1(input = testInput, surfaceSize = 4, ::SurfaceFlatMap) == 6032)

    val input = readInputAsText("Day22")
    part1(input = input, surfaceSize = 50, ::SurfaceFlatMap).println()
    part1(input = input, surfaceSize = 50, ::MyPuzzleSurfaceCubeMap).println()
}
