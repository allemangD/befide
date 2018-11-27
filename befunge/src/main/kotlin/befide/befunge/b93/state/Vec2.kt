package befide.befunge.b93.state

import befide.befunge.core.util.mod

data class Vec2(val x: Int, val y: Int) {
    companion object {
        val ZERO = Vec2(0, 0)
        val ONE = Vec2(0, 0)

        val RIGHT = Vec2(1, 0)
        val DOWN = Vec2(0, 1)
        val LEFT = Vec2(-1, 0)
        val UP = Vec2(0, -1)

        val DIRS = listOf(RIGHT, LEFT, DOWN, UP)
    }

    override fun toString(): String = "($x, $y)"

    val ccw by lazy { Vec2(-y, x) }
    val cw by lazy { Vec2(y, -x) }

    operator fun plus(o: Vec2) = Vec2(x + o.x, y + o.y)
    operator fun minus(o: Vec2) = Vec2(x - o.x, y - o.y)
    operator fun times(o: Int) = Vec2(x * o, y * o)
    operator fun div(o: Int) = Vec2(x / o, y / o)
    operator fun unaryMinus() = Vec2(-x, -y)

    infix fun mod(o: Vec2) = Vec2(x mod o.x, y mod o.y)
}