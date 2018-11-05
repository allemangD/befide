package befide.befunge.events

import befide.befunge.state.Value

data class StackEvent(val op: StackAction, val values: List<Value>)