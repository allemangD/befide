package befide.befunge.b93

import befide.befunge.b93.state.*
import befide.befunge.core.*
import befide.befunge.core.events.IpChange
import befide.befunge.core.events.StackChange
import befide.befunge.core.util.readAll
import befide.befunge.core.util.Event
import java.io.PipedReader
import java.io.PipedWriter
import java.util.*


class Interpreter93(stdinSrc: PipedWriter, stdoutDest: PipedReader)
    : MutableInterpreter<Vec2, LongData, PointerMode>() {

    override val ip = Pointer93()
    override val funge = Funge93()
    override val stack = Stack<LongData>()

    override fun stackDefault() = LongData.ZERO

    override val onIpChange = Event<IpChange<Vec2, PointerMode>>()
    override val onStackChange = Event<StackChange<LongData>>()

    override val stdin: PipedReader = PipedReader(stdinSrc)
    override val stdout: PipedWriter = PipedWriter(stdoutDest)

    override val instructionSet = B93Instructions() + B93Extras()

    override fun reset() {
        stdin.readAll()
        stdout.flush()
    }
}

fun main(args: Array<String>) {
    val stdinSrc = PipedWriter()
    val stdoutDest = PipedReader()

    val int = Interpreter93(stdinSrc, stdoutDest)

    int.onIpChange += {
        println(it.to.pos)
    }

//    int.funge.src = """2>:3g" "-!v\  g30          <
// |!`"O":+1_:.:03p>03g+:"O"`|
// @               ^  p3\" ":<
//2 234567890123456789012345678901234567890123456789012345678901234567890123456789"""

    int.funge.src = """048ce.....@"""

    while (int.ip.mode != PointerMode.Terminated) {
        int.step()
    }

    print(stdoutDest.readAll())
}