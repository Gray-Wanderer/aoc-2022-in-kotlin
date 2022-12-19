import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <T> transform(input: List<String>, acceptorCreator: () -> T): List<T>
    where T : StringAcceptor {
    val out = mutableListOf<T>()

    var acceptor: T? = null
    for (s in input) {
        if (acceptor == null) {
            acceptor = acceptorCreator()
        }
        acceptor.let {
            if (it.applyString(s)) {
                out.add(it)
                acceptor = null
            }
        }
    }

    acceptor?.let { out.add(it) }

    return out
}

interface StringAcceptor {
    fun applyString(input: String): Boolean
}