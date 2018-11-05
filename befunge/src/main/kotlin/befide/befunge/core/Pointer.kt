package befide.befunge.core

import befide.befunge.events.Event
import befide.befunge.events.IpEvent
import befide.befunge.state.IpMode
import befide.befunge.state.Vec

interface Pointer {
    val pos: Vec
    val delta: Vec
    val mode: IpMode
}