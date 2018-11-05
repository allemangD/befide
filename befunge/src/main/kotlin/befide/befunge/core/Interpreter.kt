package befide.befunge.core

import befide.befunge.events.Event
import befide.befunge.events.FungeEvent
import befide.befunge.events.IpEvent
import befide.befunge.events.StackEvent
import befide.befunge.state.Value
import java.util.*

interface Interpreter {
    val funge: Funge
    val stack: Stack<Value>
    val ip: Pointer

    val fungeChanged: Event<FungeEvent>
    val stackChanged: Event<StackEvent>
    val ipChanged: Event<IpEvent>

    fun step(): Boolean
    fun reset()
}

