package befide.befunge.b93

import befide.befunge.core.*
import befide.befunge.state.*

fun <T> List<T>.padEnd(size: Int, factory: (Int) -> (T)): List<T> = this + (this.size until size).map { factory(it) }
fun <T> List<T>.padEnd(size: Int, value: T): List<T> = this.padEnd(size) { value }

class B93Funge : Funge {
    override val width = 80
    override val height = 25

    val bounds = Vec(width, height)

    private var cars = Array(height) { Array(width) { Value(' ') } }

    override fun get(vec: Vec): Value {
        return cars[vec.y][vec.x]
    }

    override fun set(vec: Vec, value: Value) {
        cars[vec.y][vec.x] = value
    }

    override fun nextVec(vec: Vec, delta: Vec): Vec {
        return (vec + delta) mod bounds
    }

    override fun setString(data: String) {
        cars = data.split("\n").map {
            it.map(::Value).padEnd(width, Value(' ')).toTypedArray()
        }.padEnd(height) {
            Array(width) { Value(' ') }
        }.toTypedArray()
    }

    override fun toString(): String {
        return cars.joinToString("\n") { row ->
            row.joinToString("") { value ->
                (value.asChar ?: '?').toString()
            }
        }
    }
}

