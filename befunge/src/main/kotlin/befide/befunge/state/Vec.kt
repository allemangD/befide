package befide.befunge.state

infix fun Int.mod(other: Int): Int {
    val x = this % other
    return if (x >= 0) x else (x + other)
}

data class Vec(val x: Int, val y: Int) {
    operator fun plus(other: Vec) = Vec(x + other.x, y + other.y)
    operator fun times(c: Int) = Vec(x * c, y * c)

    infix fun mod(other: Vec) = Vec(x mod other.x, y mod other.y)
}