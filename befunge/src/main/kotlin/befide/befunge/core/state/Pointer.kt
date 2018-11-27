package befide.befunge.core.state

interface Pointer<V, M : Enum<M>> {
    val pos: V
    val delta: V
    val mode: M

    fun copy(): Pointer<V, M>
}