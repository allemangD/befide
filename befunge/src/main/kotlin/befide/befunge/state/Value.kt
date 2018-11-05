package befide.befunge.state

data class Value(val value: Int) {
    constructor(value: Char) : this(value.toInt())

    val asChar: Char? = if (value in (32..126)) value.toChar() else null
}