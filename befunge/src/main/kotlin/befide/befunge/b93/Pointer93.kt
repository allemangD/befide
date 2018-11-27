package befide.befunge.b93

import befide.befunge.core.MutablePointer

enum class PointerMode {
    Normal, String, Terminated
}

data class Pointer93(override var pos: Vec2 = Vec2.ZERO,
                     override var delta: Vec2 = Vec2.RIGHT,
                     override var mode: PointerMode = PointerMode.Normal)
    : MutablePointer<Vec2, PointerMode>