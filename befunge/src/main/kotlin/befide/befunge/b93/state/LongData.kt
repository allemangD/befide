package befide.befunge.b93.state

import befide.befunge.core.state.Data
import befide.befunge.core.util.mod

data class LongData(val data: Long)
    : Data {
    companion object {
        val SPACE = LongData(' ')
        val ZERO = LongData(0)
    }

    override val char = data.toChar().takeUnless { it.isISOControl() }

    constructor(char: Char) : this(char.toLong())

    operator fun plus(o: LongData) = LongData(data + o.data)
    operator fun minus(o: LongData) = LongData(data - o.data)
    operator fun times(o: LongData) = LongData(data * o.data)
    operator fun div(o: LongData) = LongData(data / o.data)
    operator fun unaryMinus() = LongData(-data)

    infix fun mod(o: LongData) = LongData(data mod o.data)

    override fun toString() = "${char ?: '\u2022'} ($data)"
}