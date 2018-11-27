package befide.befunge.core.state

interface MutableFunge<V, D : Data>
    : Funge<V, D> {

    override var data: Map<V, D>
    override var src: String

    operator fun set(pos: V, data: D)

    fun clear()
}