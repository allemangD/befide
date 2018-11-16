package befide.befunge.b93

import befide.befunge.core.Pointer
import befide.befunge.state.IpMode
import befide.befunge.state.Vec

data class B93Pointer(override var pos: Vec, override var delta: Vec, override var mode: IpMode) : Pointer