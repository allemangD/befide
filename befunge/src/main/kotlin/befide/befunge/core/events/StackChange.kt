package befide.befunge.core.events

import befide.befunge.core.state.Data

enum class StackOp { Push, Pop }

data class StackChange<D : Data>
(val op: StackOp, val data: D)