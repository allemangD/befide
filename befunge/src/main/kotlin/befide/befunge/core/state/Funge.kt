package befide.befunge.core.state

import befide.befunge.core.events.FungeChange
import befide.befunge.core.util.Event

interface Funge<V, D : Data> {
    val size: V

    val data: Map<V, D>
    val src: String

    fun defaultData(): D
    fun defaultChar(): Char

    fun next(pos: V, delta: V): V

    operator fun get(pos: V): D

    val onChange: Event<FungeChange<V, D>>
}