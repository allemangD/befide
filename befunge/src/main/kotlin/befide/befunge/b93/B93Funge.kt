package befide.befunge.b93

import befide.befunge.core.*
import befide.befunge.state.*

class B93Funge : Funge {
    override val width = 80
    override val height = 25
    private var cars = Array(height) { Array(width) { ' '.toLong() } }

    override fun get(vec: Vec): Value {
        return Value(cars[vec.y][vec.x])
    }

    override fun set(vec: Vec, value: Value) {
        cars[vec.y][vec.x] = value.value
    }

    override fun nextVec(vec: Vec, delta: Vec): Vec {
        var x = vec.x + delta.x
        var y = vec.y + delta.y
        if (x >= width || x < 0) {
            x %= width
        }
        if (x < 0) {
            x += width
        }
        if (y >= height || y < 0) {
            y %= height
        }
        if (y < 0) {
            y += height
        }

        return Vec(x, y)
    }

    override fun setString(data: String) {
        val strings = data.split('\n')
        for (i in strings.size until height) {
            cars[i] = Array(width) { ' '.toLong() }
        }
        strings.map {
            it.toList().map { it.toLong() }
        }
                .forEachIndexed { index, list ->
                    if (index > height) {
                        return
                    }
                    cars[index] = (
                            list.toList() + List(
                                    if (list.size <= width) width - list.size else 0
                            ) {
                                ' '.toLong()
                            }
                            )
                            .subList(0, width)
                            .toTypedArray()
                }
    }

    override fun toString(): String {
        return cars.map { it.map { Value(it).asChar ?: '?' }.joinToString("") }.joinToString("\n")
    }
}

