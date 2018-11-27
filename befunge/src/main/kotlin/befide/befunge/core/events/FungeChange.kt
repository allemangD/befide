package befide.befunge.core.events

import befide.befunge.core.state.Data
import befide.befunge.core.state.Funge

data class FungeChange<V, D : Data>
(val pos: V,
 val from: D,
 val to: D)