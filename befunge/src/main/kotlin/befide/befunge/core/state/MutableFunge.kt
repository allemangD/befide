package befide.befunge.core.state

interface MutableFunge<V, D : Data>
    : Funge<V, D> {

    override var data: List<List<D>>
    override var src: String

    operator fun set(pos: V, data: D)
}