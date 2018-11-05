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

    /**
     * Get the next position to be executed starting at [vec] and stepping by [delta]
     */
    fun nextVec(vec: Vec, delta: Vec): Vec

    /**
     * Set the contents of the funge based on a string, handling newlines appropriately
     */
    fun setString(data: String)

    /**
     * @return An iterable of executable positions, starting at [vec] and stepping by [delta]
     */
    fun nextVecs(vec: Vec, delta: Vec): Iterable<Vec> {
        return generateSequence(vec) {
            nextVec(it, delta)
        }.asIterable()
    }

    /**
     * Starting at [vec] and stepping by [delta], fill those positions with values in [data]
     */
    fun setMany(vec: Vec, delta: Vec, data: List<Value>) {
        for ((e, v) in data.zip(nextVecs(vec, delta)))
            set(v, e)
    }

    /**
     * @return An iterable of those values at the executable positions starting at [vec] and stepping by [delta].
     */
    fun getMany(vec: Vec, delta: Vec, count: Int): Iterable<Value> {
        return nextVecs(vec, delta).take(count).map(this::get).asIterable()
    }
}