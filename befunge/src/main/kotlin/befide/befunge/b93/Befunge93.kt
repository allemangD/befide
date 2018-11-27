package befide.befunge.b93

import befide.befunge.b93.state.*
import befide.befunge.core.*
import befide.befunge.core.util.readAll
import java.io.PipedReader
import java.io.PipedWriter
import java.util.*


class Interpreter93(stdinSrc: PipedWriter, stdoutDest: PipedReader)
    : MutableInterpreter<Vec2, Data93, PointerMode>() {

    override val ip = Pointer93()
    override val funge = Funge93()
    override val stack = Stack<Data93>()

    override fun stackDefault() = Data93.ZERO

    override val stdin: PipedReader = PipedReader(stdinSrc)
    override val stdout: PipedWriter = PipedWriter(stdoutDest)

    override val instructionSet = B93Instructions() + B93Extras()
}

fun main(args: Array<String>) {
    val stdinSrc = PipedWriter()
    val stdoutDest = PipedReader()

    val int = Interpreter93(stdinSrc, stdoutDest)

    int.funge.onChange += { (pos, from, to) ->
        println("$pos from $from to $to")
    }

    int.onIpChange += { (_, to) ->
        println("ip to ${to.pos}")
    }

    int.funge.src = """2>:3g" "-!v\  g30          <
 |!`"O":+1_:.:03p>03g+:"O"`|
 @               ^  p3\" ":<
2 234567890123456789012345678901234567890123456789012345678901234567890123456789"""

//    int.funge.src = """fedc;ba9876;54321>:#._@"""

    while (int.ip.mode != PointerMode.Terminated) {
        int.step()
    }

    print(stdoutDest.readAll())
}