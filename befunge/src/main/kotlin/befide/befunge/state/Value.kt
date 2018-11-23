package befide.befunge.state

data class Value(val value: Long) {
    constructor(value: Char) : this(value.toLong())

    val asChar: Char? = if (value in (32..127)) value.toChar() else null
}