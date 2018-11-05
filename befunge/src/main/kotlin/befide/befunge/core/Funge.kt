package befide.befunge.core

import befide.befunge.events.Event
import befide.befunge.events.FungeEvent
import befide.befunge.state.Value
import befide.befunge.state.Vec

interface Funge {
    val width: Int
    val height: Int

    operator fun get(vec: Vec): Value
    operator fun set(vec: Vec, value: Value)

    fun nextVec(vec: Vec, delta: Vec): Vec

    fun setString(data: String)

    fun nextVecs(vec: Vec, delta: Vec): Iterable<Vec> {
        return generateSequence(vec) {
            nextVec(it, delta)
        }.asIterable()
    }

    fun setMany(vec: Vec, delta: Vec, data: List<Value>) {
        for ((e, v) in data.zip(nextVecs(vec, delta)))
            set(v, e)
    }

    fun getMany(vec: Vec, delta: Vec, count: Int): Iterable<Value> {
        return nextVecs(vec, delta).take(count).map(this::get).asIterable()
    }
}