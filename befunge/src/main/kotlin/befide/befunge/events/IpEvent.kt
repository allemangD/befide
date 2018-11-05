package befide.befunge.events

import befide.befunge.core.Pointer

data class IpEvent(val from: Pointer, val to: Pointer)