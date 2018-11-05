package befide.befunge.b93

import befide.befunge.core.Pointer
import befide.befunge.state.IpMode
import befide.befunge.state.Vec

data class B93Pointer(override val pos: Vec, override val delta: Vec, override val mode: IpMode) : Pointer