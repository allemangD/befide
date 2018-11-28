package befide.befunge.core

import befide.befunge.core.events.IpChange
import befide.befunge.core.events.StackChange
import befide.befunge.core.state.Data
import befide.befunge.core.state.Funge
import befide.befunge.core.state.Pointer
import befide.befunge.core.util.Event
import java.io.PipedReader
import java.io.PipedWriter
import java.io.Reader
import java.io.Writer

interface Interpreter<V, D : Data, M : Enum<M>> {
    val ip: Pointer<V, M>
    val funge: Funge<V, D>
    val stack: List<D>

    fun stackDefault(): D

    val mode: M get() = ip.mode
    val delta: V get() = ip.delta
    val pos: V get() = ip.pos
    val instr: D get() = funge[ip.pos]
    val next: D get() = funge[funge.next(pos, delta)]

    val onIpChange: Event<IpChange<V, M>>
    val onStackChange: Event<StackChange<D>>

    var stdin: Reader?
    val stdout: Writer?

    fun step()
    fun run(afterEach: () -> Unit)
}

