package befide.befunge.b93.state

import befide.befunge.core.state.MutablePointer
import befide.befunge.core.state.Pointer

enum class PointerMode {
    Normal, String, Terminated
}

data class Pointer93(override var pos: Vec2 = Vec2.ZERO,
                     override var delta: Vec2 = Vec2.RIGHT,
                     override var mode: PointerMode = PointerMode.Normal)
    : MutablePointer<Vec2, PointerMode> {

    override fun copy(): Pointer<Vec2, PointerMode> = Pointer93(pos, delta, mode)
}