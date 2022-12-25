class TreetopTreeHouse(trees: List<String>) {
    private val treeMap: Array<IntArray>

    init {
        treeMap = Array(trees[0].length) { IntArray(trees.size) }
        for ((i, line) in trees.withIndex()) {
            for (j in line.indices) {
                treeMap[i][j] = line[j].code - '0'.code
            }
        }
    }

    val totalVisible: Int
        get() {
            var totalVisible = 0
            for (i in treeMap.indices) {
                for (j in treeMap[i].indices) {
                    if (isVisible(i, j)) {
                        totalVisible++
                    }
                }
            }
            return totalVisible
        }
    val maxObserveScore: Int
        get() {
            var maxObserveScore = 0
            for (i in treeMap.indices) {
                for (j in treeMap[i].indices) {
                    val score = getObserveScore(i, j)
                    if (score > maxObserveScore) {
                        maxObserveScore = score
                    }
                }
            }
            return maxObserveScore
        }

    private fun isVisible(k: Int, s: Int): Boolean {
        if (k == 0 || k == treeMap.size - 1 || s == 0 || s == treeMap[0].size - 1) {
            return true
        }
        val heigth = treeMap[k][s]
        var visible = 0
        for (i in 0 until k) {
            if (treeMap[i][s] >= heigth) {
                visible += 1
                break
            }
        }
        for (i in k + 1 until treeMap.size) {
            if (treeMap[i][s] >= heigth) {
                visible += 1
                break
            }
        }
        for (j in 0 until s) {
            if (treeMap[k][j] >= heigth) {
                visible += 1
                break
            }
        }
        for (j in s + 1 until treeMap[0].size) {
            if (treeMap[k][j] >= heigth) {
                visible += 1
                break
            }
        }
        return visible < 4
    }

    private fun getObserveScore(k: Int, s: Int): Int {
        val heigth = treeMap[k][s]
        var o1 = 0
        for (i in k - 1 downTo 0) {
            o1++
            if (treeMap[i][s] >= heigth) {
                break
            }
        }
        var o2 = 0
        for (i in k + 1 until treeMap.size) {
            o2++
            if (treeMap[i][s] >= heigth) {
                break
            }
        }
        var o3 = 0
        for (j in s - 1 downTo 0) {
            o3++
            if (treeMap[k][j] >= heigth) {
                break
            }
        }
        var o4 = 0
        for (j in s + 1 until treeMap[0].size) {
            o4++
            if (treeMap[k][j] >= heigth) {
                break
            }
        }
        return o1 * o2 * o3 * o4
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return TreetopTreeHouse(input).totalVisible
    }
    fun part2(input: List<String>): Int {
        return TreetopTreeHouse(input).maxObserveScore
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}