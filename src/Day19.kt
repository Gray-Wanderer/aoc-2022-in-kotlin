import java.util.regex.Pattern
import kotlin.math.max

val BLUEPRINT_PATTERN: Pattern = Pattern.compile(
    "Blueprint (\\d+): " +
            "Each ore robot costs (\\d+) ore\\. " +
            "Each clay robot costs (\\d+) ore\\. " +
            "Each obsidian robot costs (\\d+) ore and (\\d+) clay\\. " +
            "Each geode robot costs (\\d+) ore and (\\d+) obsidian\\."
)

class Blueprint(blueprint: String) {
    private val id: Int
    private val oreRobot: Robot
    private val clayRobot: Robot
    private val obsidianRobot: Robot
    private val geodeRobot: Robot
    private var maxGeodeCount = 0
    private var maxTime = 0

    init {
        val matcher = BLUEPRINT_PATTERN.matcher(blueprint)
        if (!matcher.find()) {
            throw RuntimeException()
        }
        id = matcher.group(1).toInt()
        val oreRobotCost = Resources(ore = matcher.group(2).toInt())
        val clayRobotCost = Resources(ore = matcher.group(3).toInt())
        val obsidianRobotCost = Resources(ore = matcher.group(4).toInt(), clay = matcher.group(5).toInt())
        val geodeRobotCost = Resources(ore = matcher.group(6).toInt(), obsidian = matcher.group(7).toInt())
        oreRobot = Robot(
            name = "ore",
            limit = listOf(oreRobotCost.ore, clayRobotCost.ore, obsidianRobotCost.ore, geodeRobotCost.ore).max(),
            producePerMinute = Resources(ore = 1),
            cost = oreRobotCost
        )
        clayRobot = Robot(
            name = "clay",
            limit = obsidianRobotCost.clay,
            producePerMinute = Resources(clay = 1),
            cost = clayRobotCost
        )
        obsidianRobot = Robot(
            name = "obsidian",
            limit = geodeRobotCost.obsidian,
            producePerMinute = Resources(obsidian = 1),
            cost = obsidianRobotCost
        )
        geodeRobot = Robot(
            name = "geode",
            limit = Int.MAX_VALUE,
            producePerMinute = Resources(geodes = 1),
            cost = geodeRobotCost
        )
    }

    fun getQuality(minutes: Int): Int {
        return id * getMaxMaxGeodes(minutes)
    }

    fun getMaxMaxGeodes(minutes: Int): Int {
        maxTime = minutes
        calcMaxGeodes(
            minute = 0,
            state = State(robots = mapOf(Pair(oreRobot, 1)), resources = Resources())
        )
        println("Finished $id")
        return maxGeodeCount
    }

    private fun calcMaxGeodes(minute: Int, state: State?) {
        if (state == null) {
            return
        }
        if (minute >= maxTime) {
            maxGeodeCount = max(maxGeodeCount, state.resources.geodes)
            return
        }
        val currentGeodeRobots = state.robots.getOrDefault(geodeRobot, 0)
        val time = maxTime - minute
        val newGeodes = currentGeodeRobots * time + time * (time + 1) / 2
        if (state.resources.geodes + newGeodes < maxGeodeCount) {
            return
        }

        val nextMinute = minute + 1
        if (state.robots[clayRobot] == null) {
            calcMaxGeodes(minute = nextMinute, state = state.getNextState(clayRobot))
            if (state.resources.ore <= oreRobot.limit * 1.5) {
                calcMaxGeodes(minute = nextMinute, state = state.getNextState(oreRobot))
            }
            if (state.resources.ore < oreRobot.limit * 2) {
                calcMaxGeodes(minute = nextMinute, state = state.getNextState(null))
            }
        } else if (state.robots[obsidianRobot] == null) {
            calcMaxGeodes(minute = nextMinute, state = state.getNextState(obsidianRobot))
            if (state.resources.clay <= clayRobot.limit * 1.5) {
                calcMaxGeodes(minute = nextMinute, state = state.getNextState(clayRobot))
            }
            if (state.resources.ore <= oreRobot.limit * 1.5) {
                calcMaxGeodes(minute = nextMinute, state = state.getNextState(oreRobot))
            }
            if (state.resources.clay < clayRobot.limit * 2) {
                calcMaxGeodes(minute = nextMinute, state = state.getNextState(null))
            }
        } else if (state.robots[geodeRobot] == null) {
            calcMaxGeodes(minute = nextMinute, state = state.getNextState(geodeRobot))
            if (state.resources.obsidian < obsidianRobot.limit * 2) {
                calcMaxGeodes(minute = nextMinute, state = state.getNextState(null))
            }
            calcMaxGeodes(minute = nextMinute, state = state.getNextState(obsidianRobot))
            if (state.resources.clay <= clayRobot.limit * 1.5) {
                calcMaxGeodes(minute = nextMinute, state = state.getNextState(clayRobot))
            }
        } else {
            calcMaxGeodes(minute = nextMinute, state = state.getNextState(geodeRobot))
            calcMaxGeodes(minute = nextMinute, state = state.getNextState(obsidianRobot))
            calcMaxGeodes(minute = nextMinute, state = state.getNextState(null))
        }
    }
}

class Robot(val name: String, val limit: Int, val cost: Resources, val producePerMinute: Resources) {
    override fun toString(): String {
        return "Robot $name"
    }
}

class State(val robots: Map<Robot, Int> = mapOf(), val resources: Resources) {
    private val producePerMinute: Resources = Resources()

    init {
        robots.forEach { (robot, count) ->
            producePerMinute.ore += robot.producePerMinute.ore * count
            producePerMinute.clay += robot.producePerMinute.clay * count
            producePerMinute.obsidian += robot.producePerMinute.obsidian * count
            producePerMinute.geodes += robot.producePerMinute.geodes * count
        }
    }

    private fun canBuildRobot(r: Robot): Boolean {
        return resources include r.cost && robots.getOrDefault(r, 0) < r.limit
    }

    fun getNextState(newRobot: Robot? = null): State? {
        if (newRobot != null && !this.canBuildRobot(newRobot)) {
            return null
        }
        val nextResources = Resources(resources)
        nextResources.add(producePerMinute)
        return if (newRobot != null) {
            nextResources.subtract(newRobot.cost)
            val newRobots = this.robots.toMap().plus(Pair(newRobot, robots.getOrDefault(newRobot, 0) + 1))
            State(
                robots = newRobots,
                resources = nextResources
            )
        } else {
            State(
                robots = this.robots,
                resources = nextResources
            )
        }
    }
}

class Resources(var ore: Int = 0, var clay: Int = 0, var obsidian: Int = 0, var geodes: Int = 0) {
    constructor(r: Resources) : this(r.ore, r.clay, r.obsidian, r.geodes)

    infix fun include(r: Resources): Boolean {
        return r.ore <= this.ore
                && r.clay <= this.clay
                && r.obsidian <= this.obsidian
                && r.geodes <= this.geodes
    }

    fun add(r: Resources) {
        this.ore += r.ore
        this.clay += r.clay
        this.obsidian += r.obsidian
        this.geodes += r.geodes
    }

    fun subtract(r: Resources) {
        this.ore -= r.ore
        this.clay -= r.clay
        this.obsidian -= r.obsidian
        this.geodes -= r.geodes
    }

    override fun toString(): String {
        return "(ore=$ore, clay=$clay, obsidian=$obsidian, geodes=$geodes)"
    }

}

fun main() {
    fun part1(input: List<String>): Int {
        println("Started...")
        val result = input.sumOf { Blueprint(it).getQuality(24) }
        result.println()
        return result
    }

    fun part2(input: List<String>): Int {
        println("Started...")
        val maxGeodes = input.map { Blueprint(it).getMaxMaxGeodes(32) }
        var result = 1
        maxGeodes.forEach { result *= it }
        result.println()
        return result
    }

    val testInput = readInput("Day19_test")
    check(part1(testInput) == 33)
    check(part2(testInput) == 3472)

    val input = readInput("Day19")
    part1(input).println()
    part2(input.subList(0, 3)).println()
}