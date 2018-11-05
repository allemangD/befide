package befide.befunge.events

import befide.befunge.state.Value
import befide.befunge.state.Vec

data class FungeEvent(val vec: Vec, val from: Value, val to: Value)