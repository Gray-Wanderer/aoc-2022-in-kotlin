const val TOTAL_FS_SIZE: Long = 70000000

class NoSpaceLeftOnDevice {
    private val fs = FsNode()
    private val allNodes: MutableList<FsNode> = ArrayList()
    fun parseFs(commands: List<String>) {
        allNodes.add(fs)
        var currentNode: FsNode? = fs
        for (command in commands) {
            val parts = command.split(" ").dropLastWhile { it.isEmpty() }.toTypedArray()
            if ("$" == parts[0]) {
                if ("cd" == parts[1]) {
                    currentNode = when (parts[2]) {
                        ".." -> currentNode!!.parent
                        "/" -> fs
                        else -> currentNode!!.goToDir(parts[2])
                    }
                }
            } else {
                if ("dir" == parts[0]) {
                    val newDir = currentNode!!.addDir(parts[1])
                    if (newDir != null) {
                        allNodes.add(newDir)
                    }
                } else {
                    currentNode!!.addFile(parts[1], java.lang.Long.valueOf(parts[0]))
                }
            }
        }
    }

    fun getSumForDirSizeMoreThan(threshold: Long): Long {
        return allNodes.stream()
            .filter { n: FsNode -> n.dirSize < threshold }
            .mapToLong { obj: FsNode -> obj.dirSize }
            .sum()
    }

    fun smallsetDirSizeToFreeSpace(freeSpace: Long): Long {
        val totalFreeSize = TOTAL_FS_SIZE - fs.dirSize
        return allNodes.stream()
            .filter { n: FsNode -> totalFreeSize + n.dirSize >= freeSpace }
            .mapToLong { obj: FsNode -> obj.dirSize }
            .min()
            .orElseGet { fs.dirSize }
    }
}

class FsNode(val parent: FsNode? = null) {
    private var size: Long = 0
    private var actualSize = true
    private val dirs: MutableMap<String, FsNode?> = HashMap()
    private val files: MutableMap<String, File> = HashMap()
    fun addDir(name: String): FsNode? {
        if (dirs.containsKey(name)) {
            return null
        }
        val child = FsNode(this)
        dirs[name] = child
        actualSize = false
        return child
    }

    fun goToDir(name: String): FsNode? {
        return dirs.getOrDefault(name, null)
    }

    fun addFile(name: String, size: Long) {
        if (files.containsKey(name)) return
        files[name] = File(size)
        actualSize = false
    }

    val dirSize: Long
        get() {
            if (actualSize) {
                return size
            }
            size = dirs.values.stream()
                .mapToLong { obj: FsNode? -> obj!!.dirSize }
                .sum()
            size += files.values.stream()
                .mapToLong { obj: File -> obj.size }
                .sum()
            actualSize = true
            return size
        }
}

class File(val size: Long)

fun main() {
    fun part1(input: List<String>): Long {
        val s = NoSpaceLeftOnDevice()
        s.parseFs(input)
        return s.getSumForDirSizeMoreThan(100000)
    }

    fun part2(input: List<String>): Long {
        val s = NoSpaceLeftOnDevice()
        s.parseFs(input)
        return s.smallsetDirSizeToFreeSpace(30000000)
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437L)
    check(part2(testInput) == 24933642L)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}