package befide.befunge.state

data class Value(val value: Long) {
    constructor(value: Char) : this(value.toLong())

    val asChar: Char? = if (value in (32..126)) value.toChar() else null
}