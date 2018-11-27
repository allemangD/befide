package befide.befunge.core.state

interface MutablePointer<V, M : Enum<M>>
    : Pointer<V, M> {

    override var pos: V
    override var delta: V
    override var mode: M
}