package befide.befunge.core

import befide.befunge.core.events.IpChange
import befide.befunge.core.events.StackChange
import befide.befunge.core.events.StackOp
import befide.befunge.core.state.Data
import befide.befunge.core.state.MutableFunge
import befide.befunge.core.state.MutablePointer
import befide.befunge.core.util.Event
import befide.befunge.core.util.readAll
import java.util.*

abstract class MutableInterpreter<V, D : Data, M : Enum<M>>
    : Interpreter<V, D, M> {
    abstract override val ip: MutablePointer<V, M>
    abstract override val funge: MutableFunge<V, D>
    abstract override val stack: Stack<D>

    abstract val instructionSet: InstructionSet<V, D, M>

    override val onIpChange: Event<IpChange<V, M>> = Event()
    override val onStackChange: Event<StackChange<D>> = Event()

    override fun step() {
        instructionSet.step(this)
        move()
    }

    fun notifyIp(op: () -> Unit) {
        val from = ip.copy()
        op()
        val to = ip.copy()
        onIpChange(IpChange(from, to))
    }

    fun notifyStack(op: StackOp, block: () -> D): D {
        val res = block()
        onStackChange(StackChange(op, res))
        return res
    }

    override var mode: M
        get() = ip.mode
        set(mode) = notifyIp { ip.mode = mode }

    override var delta: V
        get() = ip.delta
        set(delta) = notifyIp { ip.delta = delta }

    override var pos: V
        get() = ip.pos
        set(pos) = notifyIp { ip.pos = pos }

    fun move(delta: V? = null) {
        pos = funge.next(pos, delta ?: this.delta)
    }

    fun pop(): D = notifyStack(StackOp.Pop) {
        return@notifyStack if (stack.empty()) stackDefault() else stack.pop()
    }

    fun pop(n: Int): List<D> {
        return (0 until n).map { pop() }
    }

    fun push(vararg data: D) {
        for (datum in data) {
            notifyStack(StackOp.Push) { stack.push(datum) }
        }
    }

    override fun reset() {
        stdin.readAll()
        stdout.flush()
    }
}