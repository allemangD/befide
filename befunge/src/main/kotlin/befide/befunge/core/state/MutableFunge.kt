package befide.befunge.core.state

import befide.befunge.core.events.FungeChange
import befide.befunge.core.util.Event

abstract class MutableFunge<V, D : Data>
    : Funge<V, D> {

    abstract override var data: Map<V, D>
    abstract override var src: String

    override val onChange = Event<FungeChange<V, D>>()

    fun notify(pos: V, op: () -> Unit) {
        val from = this[pos]
        op()
        val to = this[pos]

        onChange(FungeChange(pos, from, to))
    }

    abstract operator fun set(pos: V, data: D)

    abstract fun clear()
}