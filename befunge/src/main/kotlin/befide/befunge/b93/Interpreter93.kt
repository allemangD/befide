package befide.befunge.b93

import befide.befunge.b93.state.*
import befide.befunge.core.*
import befide.befunge.core.util.readAll
import com.sun.imageio.spi.OutputStreamImageOutputStreamSpi
import java.io.*
import java.util.*
import kotlin.concurrent.thread
import kotlin.concurrent.timer


class Interpreter93(override var stdin: Reader? = null, override var stdout: Writer? = null)
    : MutableInterpreter<Vec2, Data93, PointerMode>() {

    override val ip = Pointer93()
    override val funge = Funge93()
    override val stack = Stack<Data93>()

    override fun stackDefault() = Data93.ZERO

    override val instructionSet = B93Instructions() + B93Extras()

    override fun run(afterEach: () -> Unit) {
        while (mode != PointerMode.Terminated) {
            step()
            afterEach()
            stdout?.flush()
        }
    }
}
