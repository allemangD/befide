package befide.befunge.b93

import befide.befunge.core.Interpreter
import befide.befunge.events.Event
import befide.befunge.events.FungeEvent
import befide.befunge.events.IpEvent
import befide.befunge.events.StackEvent
import befide.befunge.state.IpMode
import befide.befunge.state.Value
import befide.befunge.state.Vec
import java.util.*

class B93Interpreter : Interpreter {
    override val funge = B93Funge()
    override val stack = Stack<Value>()
    override val ip = B93Pointer(Vec(0, 0), Vec(1, 0), IpMode.Normal)

    override val fungeChanged: Event<FungeEvent> = Event()
    override val stackChanged: Event<StackEvent> = Event()
    override val ipChanged: Event<IpEvent> = Event()

    override fun step(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reset() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}