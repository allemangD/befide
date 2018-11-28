package befide.befunge.core.util

import java.io.PipedReader
import java.io.Reader
import java.util.*
import kotlin.random.Random


infix fun Int.mod(o: Int): Int {
    var rem = this % o
    if (rem < 0) rem += o
    return rem
}

infix fun Long.mod(o: Long): Long {
    var rem = this % o
    if (rem < 0) rem += o
    return rem
}

fun <T> List<T>.chooseOne(): T = this[Random.nextInt(size)]

fun <T> Boolean.orNull(block: () -> T): T? = if (this) block() else null

fun Reader.readAll(): String = generateSequence {
    ready().orNull { read().toChar() }
}.joinToString("")
