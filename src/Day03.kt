class Rucksack1 : StringAcceptor {
    private val uniqChars1: MutableMap<Char, Int>
    private val allChars: MutableSet<Char>
    var weigth: Int
        private set

    init {
        uniqChars1 = HashMap()
        allChars = HashSet()
        weigth = 0
    }

    override fun applyString(input: String): Boolean {
        var i = 0
        while (i < input.length / 2) {
            val c = input[i]
            uniqChars1[c] = 1
            allChars.add(c)
            i++
        }
        while (i < input.length) {
            val c = input[i]
            allChars.add(c)
            if (uniqChars1.containsKey(c)) {
                uniqChars1.compute(c) { _: Char?, v: Int? -> v!! + 1 }
                weigth = calcWeight(c)
            }
            i++
        }
        return true
    }

    companion object {
        private fun calcWeight(c: Char): Int {
            return if (c in 'a'..'z') {
                c.code - 'a'.code + 1
            } else {
                c.code - 'A'.code + 27
            }
        }
    }
}

class Rucksack2 : StringAcceptor {
    private val set1: MutableSet<Char> = HashSet()
    private val set2: MutableSet<Char> = HashSet()
    private val set3: MutableSet<Char> = HashSet()
    var weigth = 0
        private set

    override fun applyString(input: String): Boolean {
        if (set1.isEmpty()) {
            addCharactersToSet(set1, input)
        } else if (set2.isEmpty()) {
            addCharactersToSet(set2, input)
        } else {
            addCharactersToSet(set3, input)
            for (c in set1) {
                if (set2.contains(c) && set3.contains(c)) {
                    weigth = calcWeight(c)
                    break
                }
            }
            return true
        }
        return false
    }

    private fun addCharactersToSet(set: MutableSet<Char>, s: String) {
        for (c in s.toCharArray()) {
            set.add(c)
        }
    }

    companion object {
        private fun calcWeight(c: Char): Int {
            return if (c in 'a'..'z') {
                c.code - 'a'.code + 1
            } else {
                c.code - 'A'.code + 27
            }
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return transform(input) { Rucksack1() }.map(Rucksack1::weigth).sum()
    }

    fun part2(input: List<String>): Int {
        return transform(input) { Rucksack2() }.map(Rucksack2::weigth).sum()
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}