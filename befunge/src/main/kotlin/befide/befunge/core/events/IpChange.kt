package befide.befunge.core.events

import befide.befunge.core.state.Pointer

data class IpChange<V, M : Enum<M>>
(val from: Pointer<V, M>, val to: Pointer<V, M>)