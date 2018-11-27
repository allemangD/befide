package befide.befunge.b93.state

import befide.befunge.core.state.Data
import befide.befunge.core.util.mod

data class Data93(val data: Long)
    : Data {
    companion object {
        val SPACE = Data93(' ')
        val ZERO = Data93(0)
    }

    override val char = data.toChar().takeUnless { it.isISOControl() }

    constructor(char: Char) : this(char.toLong())

    operator fun plus(o: Data93) = Data93(data + o.data)
    operator fun minus(o: Data93) = Data93(data - o.data)
    operator fun times(o: Data93) = Data93(data * o.data)
    operator fun div(o: Data93) = Data93(data / o.data)
    operator fun unaryMinus() = Data93(-data)

    infix fun mod(o: Data93) = Data93(data mod o.data)

    override fun toString() = "${char ?: '\u2022'} ($data)"
}