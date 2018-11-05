package befide.befunge.events

import befide.befunge.state.Value
import befide.befunge.state.Vec

data class FungeChange(val vec: Vec, val from: Value, val to: Value)

data class FungeEvent(val changes: List<FungeChange>)