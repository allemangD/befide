package befide.befunge.core.util

import java.io.PipedReader
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

fun PipedReader.readAll(): String = generateSequence {
    if (ready()) read().toChar() else null
}.joinToString("")

inline fun <reified T> Stack<T>.pop(n: Int): List<T> {
    return (0 until n).map { pop() }
}