package befide.befunge.state

data class Vec(val x: Int, val y: Int) {
    operator fun plus(other: Vec) = Vec(x + other.x, y + other.y)
    operator fun times(c: Int) = Vec(x * c, y * c)
}