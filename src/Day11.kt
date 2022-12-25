import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.function.Function


val numExtractor: Pattern = Pattern.compile("\\d+")
val operationExtractor: Pattern = Pattern.compile("new = old ([*+]) (\\w+)")

class MonkeyInTheMiddle : StringAcceptor {
    private var id = -1
    private var items = mutableListOf<Long>()
    private var monkeyOperation: Function<Long, Long>? = null
    var divisible: Long = -1
        private set
    private var trueMonkeyId = -1
    private var falseMonkeyId = -1
    var inspectedCount: Long = 0
        private set

    constructor()
    constructor(m: MonkeyInTheMiddle) {
        id = m.id
        items = m.items.toMutableList()
        monkeyOperation = m.monkeyOperation
        divisible = m.divisible
        trueMonkeyId = m.trueMonkeyId
        falseMonkeyId = m.falseMonkeyId
    }

    fun play(monkeys: List<MonkeyInTheMiddle>, worryDivisor: Long, commonMonkeysDivisor: Long) {
        while (items.isNotEmpty()) {
            val item = items.removeFirst()
            val worryLevel = monkeyOperation!!.apply(item) / worryDivisor % commonMonkeysDivisor
            if (worryLevel % divisible == 0L) {
                monkeys[trueMonkeyId].addItem(worryLevel)
            } else {
                monkeys[falseMonkeyId].addItem(worryLevel)
            }
            inspectedCount++
        }
    }

    private fun addItem(item: Long) {
        items.add(item)
    }

    override fun applyString(input: String): Boolean {
        if (input.startsWith("Monkey")) {
            val m: Matcher = numExtractor.matcher(input)
            if (m.find()) {
                id = Integer.valueOf(m.group())
            } else {
                throw RuntimeException()
            }
        } else if (input.startsWith("  Starting items:")) {
            val m: Matcher = numExtractor.matcher(input)
            while (m.find()) {
                items.add(java.lang.Long.valueOf(m.group()))
            }
        } else if (input.startsWith("  Operation:")) {
            val m: Matcher = operationExtractor.matcher(input)
            monkeyOperation = if (m.find()) {
                val operation: String = m.group(1)
                val value: String = m.group(2)
                if ("*" == operation) {
                    if ("old" == value) {
                        Function { i: Long -> i * i }
                    } else {
                        val `val`: Long = java.lang.Long.valueOf(m.group(2))
                        Function { i: Long -> i * `val` }
                    }
                } else if ("+" == operation) {
                    if ("old" == value) {
                        Function { i: Long -> i + i }
                    } else {
                        val `val`: Long = java.lang.Long.valueOf(m.group(2))
                        Function { i: Long -> i + `val` }
                    }
                } else {
                    throw RuntimeException()
                }
            } else {
                throw RuntimeException()
            }
        } else if (input.startsWith("  Test: divisible by ")) {
            val m: Matcher = numExtractor.matcher(input)
            divisible = if (m.find()) {
                Integer.valueOf(m.group()).toLong()
            } else {
                throw RuntimeException()
            }
        } else if (input.startsWith("    If true: throw to monkey ")) {
            val m: Matcher = numExtractor.matcher(input)
            if (m.find()) {
                trueMonkeyId = Integer.valueOf(m.group())
            } else {
                throw RuntimeException()
            }
        } else if (input.startsWith("    If false: throw to monkey ")) {
            val m: Matcher = numExtractor.matcher(input)
            return if (m.find()) {
                falseMonkeyId = Integer.valueOf(m.group())
                true
            } else {
                throw RuntimeException()
            }
        }
        return false
    }
}

fun main() {
    fun solve(monkeys: List<MonkeyInTheMiddle>, manageWorry: Long, allDivisibles: Long, iteratioons: Int): Long {
        var localMonkeys = monkeys.map(::MonkeyInTheMiddle)
        for (i in 0 until iteratioons) {
            localMonkeys.forEach { m -> m.play(localMonkeys, manageWorry, allDivisibles) }
        }
        localMonkeys = localMonkeys.sortedByDescending(MonkeyInTheMiddle::inspectedCount)
        return localMonkeys[0].inspectedCount * localMonkeys[1].inspectedCount
    }

    fun part1(input: List<String>): Long {
        val monkeys = transform(input, ::MonkeyInTheMiddle)
        var commonDivisor = 1L
        monkeys.forEach {
            commonDivisor *= it.divisible
        }
        return solve(monkeys = monkeys, manageWorry = 3, allDivisibles = commonDivisor, iteratioons = 20)
    }

    fun part2(input: List<String>): Long {
        val monkeys = transform(input, ::MonkeyInTheMiddle)
        var commonDivisor = 1L
        monkeys.forEach {
            commonDivisor *= it.divisible
        }
        return solve(monkeys = monkeys, manageWorry = 1, allDivisibles = commonDivisor, iteratioons = 10000)
    }

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605L)
    check(part2(testInput) == 2713310158L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}