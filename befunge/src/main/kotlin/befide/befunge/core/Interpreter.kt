package befide.befunge.core

import befide.befunge.events.*
import befide.befunge.state.Value
import java.util.*
import java.util.concurrent.BlockingQueue

/**
 * Interface for a Befunge interpreter
 *
 * @property fungeChanged Invoked whenever the [funge] is modified
 * @property stackChanged Invoked whenever the [stack] is modified
 * @property ipChanged Invoked whenever the [ip] is modified
 */
interface Interpreter {
    val funge: Funge
    val stack: Stack<Value>
    val ip: Pointer

    val fungeChanged: Event<FungeEvent>
    val stackChanged: Event<StackEvent>
    val ipChanged: Event<IpEvent>
    val outputChanged: Event<OutputEvent>

    val stdInput: Queue<Char>
    val stdOutput: Queue<String>

    /**
     * @return If [ip] is inactive after this step, indicating execution has halted, then return `false`
     */
    fun step(): Boolean

    /**
     * Reset the state of the interpreter to the last state before [step] was executed - that is, reset [funge] and [ip] to the state which was manually set, before any interpretation via [step]
     */
    fun reset()
}

