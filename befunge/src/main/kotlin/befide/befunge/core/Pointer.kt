package befide.befunge.core

import befide.befunge.events.Event
import befide.befunge.events.IpEvent
import befide.befunge.state.IpMode
import befide.befunge.state.Vec

/**
 * Represents a Befunge Instruction Pointer
 *
 * Note that [Vec] need not be a concrete type, but is simply to be easier to implement 2d Befunge
 */
interface Pointer {
    val pos: Vec
    val delta: Vec
    val mode: IpMode
}