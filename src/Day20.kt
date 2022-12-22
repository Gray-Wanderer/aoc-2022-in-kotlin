import kotlin.math.abs

class Node(val number: Long, nodeCount: Int) {
    var prev: Node = this
    var next: Node = this
    val move: Long

    init {
        var truncatedMove = abs(number)
        while (truncatedMove >= nodeCount) {
            truncatedMove = (truncatedMove % nodeCount) + (truncatedMove / nodeCount)
        }
        this.move = if (number > 0) truncatedMove else -truncatedMove
    }

    fun addNode(node: Node) {
        val nextNode = this.next
        this.next = node
        nextNode.prev = node
        node.next = nextNode
        node.prev = this
    }
}

fun moveNode(node: Node) {
    val steps = abs(node.move)

    if (steps == 0L) {
        return
    }

    var rightNode = node.prev

    node.prev.next = node.next
    node.next.prev = node.prev

    for (i in 1..steps) {
        rightNode = if (node.move < 0) rightNode.prev else rightNode.next
    }

    rightNode.addNode(node)
}

tailrec fun getForIndex(head: Node, index: Int): Node {
    return if (index == 0) head else getForIndex(head.next, index - 1)
}

fun main() {
    fun solution(input: List<String>, multiplyer: Long = 1, iterations: Int = 1): Long {
        val allNodes = input.map {
            Node(it.toLong() * multiplyer, input.size)
        }
        var head = allNodes.find { it.number == 0L } ?: allNodes[0]
        var tail = allNodes[0]

        allNodes.forEach {
            tail.addNode(it)
            tail = it
        }

        for (i in 1..iterations) {
            for (node in allNodes) {
                moveNode(node)
            }
        }

        var sum = 0L
        for (i in 1..3) {
            head = getForIndex(head, 1000)
            sum += head.number
        }
        return sum
    }

    val testInput = readInput("Day20_test")
    check(solution(input = testInput) == 3L)
    check(solution(input = testInput, multiplyer = 811589153L, iterations = 10) == 1623178306L)

    val input = readInput("Day20")
    solution(input).println()
    solution(input, multiplyer = 811589153L, iterations = 10).println()
}
