import java.lang.RuntimeException
import java.util.LinkedList
import java.util.regex.Pattern

val MONKEY_CALC_PATTERN: Pattern = Pattern.compile("(\\w+): (\\w+) ([*+-/]) (\\w+)")
val MONKEY_NUMBER_PATTERN: Pattern = Pattern.compile("(\\w+): (\\d+)")

const val ROOT_NODE_NAME = "root"
const val HUMN_NODE_NAME = "humn"

class MonkeyOperation(val monkeyName: String, val monkeyLeft: String, val monkeyRight: String, val operation: Char) {
    fun toOperationNode(prev: MonkeyOperationNode? = null): MonkeyOperationNode {
        return MonkeyOperationNode(
            name = monkeyName,
            prev = prev,
            leftName = monkeyLeft,
            rightName = monkeyRight,
            operation = operation
        )
    }
}

abstract class MonkeyNode(val name: String, var prev: MonkeyNode? = null) {
    var left: MonkeyNode? = null
    var right: MonkeyNode? = null
    abstract fun calcResult(): Long

    abstract fun rotateFor(rotateNoodeName: String)
}

class MonkeyNumberNode(name: String, private val number: Long, prev: MonkeyOperationNode) : MonkeyNode(name = name, prev = prev) {
    override fun calcResult(): Long {
        return number
    }

    override fun rotateFor(rotateNoodeName: String) {
    }
}

class MonkeyOperationNode(
    private var operation: Char,
    var leftName: String?,
    var rightName: String?,
    name: String,
    prev: MonkeyOperationNode? = null
) : MonkeyNode(name = name, prev = prev) {

    override fun calcResult(): Long {
        return if (left == null && right == null) {
            throw RuntimeException()
        } else if (name == ROOT_NODE_NAME && left == null) {
            right!!.calcResult()
        } else if (name == ROOT_NODE_NAME && right == null) {
            left!!.calcResult()
        } else {
            val leftResult = left!!.calcResult()
            val rightResult = right!!.calcResult()
            when (operation) {
                '*' -> leftResult * rightResult
                '/' -> leftResult / rightResult
                '+' -> leftResult + rightResult
                '-' -> leftResult - rightResult
                else -> throw RuntimeException()
            }
        }
    }

    override fun rotateFor(rotateNoodeName: String) {
        val oldPrev = prev
        val rotateNode = if (left?.name == rotateNoodeName) {
            left
        } else if (right?.name == rotateNoodeName) {
            right
        } else throw RuntimeException()
        prev = rotateNode

        when (operation) {
            '+', '*' -> commutativeSwap(oldPrev, rotateNode)
            '-', '/' -> notCommutativeSwap(oldPrev, rotateNode)
            else -> throw RuntimeException()
        }
        leftName = left?.name
        rightName = right?.name
        oldPrev?.rotateFor(name)
    }

    private fun commutativeSwap(oldPrev: MonkeyNode?, rotateNode: MonkeyNode?) {
        operation = when (operation) {
            '*' -> '/'
            '+' -> '-'
            else -> throw RuntimeException()
        }
        val newRight = if (left === rotateNode) right else left
        left = oldPrev
        right = newRight
    }

    private fun notCommutativeSwap(oldPrev: MonkeyNode?, rotateNode: MonkeyNode?) {
        operation = when (operation) {
            '-' -> if (left === rotateNode) '+' else '-'
            '/' -> if (left === rotateNode) '*' else '/'
            else -> throw RuntimeException()
        }
        val newLeft = if (left === rotateNode) oldPrev else left
        val newRight = if (right === rotateNode) oldPrev else right
        left = newLeft
        right = newRight
    }
}

open class MonkeyTree(input: List<String>) {
    private val operationMonkeys: MutableMap<String, MonkeyOperation> = mutableMapOf()
    private val numberMonkeys: MutableMap<String, Long> = mutableMapOf()
    private var buildedRootTree = false

    private var rootMonkeyNode: MonkeyOperationNode
    private var beforeHumnNode: MonkeyOperationNode? = null

    init {
        input.forEach {
            val monkeyCalcMatcher = MONKEY_CALC_PATTERN.matcher(it)
            if (monkeyCalcMatcher.find()) {
                operationMonkeys[monkeyCalcMatcher.group(1)] = MonkeyOperation(
                    monkeyName = monkeyCalcMatcher.group(1),
                    monkeyLeft = monkeyCalcMatcher.group(2),
                    operation = monkeyCalcMatcher.group(3)[0],
                    monkeyRight = monkeyCalcMatcher.group(4)
                )
            } else {
                val monkeyNumberMatcher = MONKEY_NUMBER_PATTERN.matcher(it)
                if (monkeyNumberMatcher.find()) {
                    numberMonkeys[monkeyNumberMatcher.group(1)] = monkeyNumberMatcher.group(2).toLong()
                } else {
                    throw RuntimeException()
                }
            }
        }
        rootMonkeyNode = operationMonkeys[ROOT_NODE_NAME]!!.toOperationNode()
    }

    private fun buildRootTree() {
        if (buildedRootTree) {
            return
        }

        val monkeysToProcess = LinkedList<MonkeyOperationNode>()
        monkeysToProcess.add(rootMonkeyNode)

        while (monkeysToProcess.isNotEmpty()) {
            val node = monkeysToProcess.poll()

            if (node.leftName == HUMN_NODE_NAME || node.rightName == HUMN_NODE_NAME) {
                beforeHumnNode = node
            }

            if (numberMonkeys[node.leftName] != null) {
                node.left = createMonkeyNumberForNode(node, node.leftName!!)
            } else {
                val leftOperation = operationMonkeys[node.leftName]!!.toOperationNode(node)
                node.left = leftOperation
                monkeysToProcess.add(leftOperation)
            }
            if (numberMonkeys[node.rightName] != null) {
                node.right = createMonkeyNumberForNode(node, node.rightName!!)
            } else {
                val rightOperation = operationMonkeys[node.rightName]!!.toOperationNode(node)
                node.right = rightOperation
                monkeysToProcess.add(rightOperation)
            }
        }
        this.buildedRootTree = true
    }

    private fun createMonkeyNumberForNode(node: MonkeyOperationNode, numberNodeName: String): MonkeyNumberNode {
        return MonkeyNumberNode(
            name = numberNodeName,
            prev = node,
            number = numberMonkeys[numberNodeName]!!
        )
    }

    fun calcForRoot(): Long {
        buildRootTree()
        return rootMonkeyNode.calcResult()
    }

    fun calcForHumn(): Long {
        buildRootTree()
        beforeHumnNode!!.rotateFor(HUMN_NODE_NAME)
        return beforeHumnNode!!.calcResult()
    }
}

fun main() {
    val testTree = MonkeyTree(readInput("Day21_test"))
    check(testTree.calcForRoot() == 152L)
    check(testTree.calcForHumn() == 301L)

    val inputTree = MonkeyTree(readInput("Day21"))
    inputTree.calcForRoot().println()
    inputTree.calcForHumn().println()
}
