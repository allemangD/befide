package befide.befunge.b93

import befide.befunge.core.*
import befide.befunge.core.util.chooseOne
import befide.befunge.core.util.readAll
import befide.befunge.events.Event
import java.io.PipedReader
import java.io.PipedWriter
import java.util.*


class Interpreter93(stdinSrc: PipedWriter, stdoutDest: PipedReader)
    : Interpreter<Vec2, LongData, PointerMode> {

    override val ip = Pointer93()
    override val funge = Funge93()
    override val stack = Stack<LongData>()

    override val onIpChange = Event<IpChange<Vec2, PointerMode>>()
    override val onStackChange = Event<StackChange<LongData>>()

    override val stdin: PipedReader = PipedReader(stdinSrc)
    override val stdout: PipedWriter = PipedWriter(stdoutDest)

    private fun notifyIp(op: () -> Unit) {
        val from = ip.copy()
        op()
        val to = ip.copy()
        onIpChange(IpChange(from, to))
    }

    private fun move(dir: Vec2? = null) = notifyIp {
        ip.pos = funge.next(ip.pos, dir ?: ip.delta)
    }

    private fun pop(): LongData {
        val d = if (stack.empty()) LongData.ZERO else stack.pop()
        onStackChange(StackChange(StackOp.Pop, d))
        return d
    }

    private fun push(vararg data: LongData) {
        for (d in data) {
            stack.push(d)
            onStackChange(StackChange(StackOp.Push, d))
        }
    }

    private fun argPop(n: Int, op: (Array<LongData>) -> Unit) {
        val args = (0 until n).map { pop() }.toTypedArray()
        op(args)
    }

    private var mode
        get() = ip.mode
        set(mode) = notifyIp { ip.mode = mode }

    private var delta
        get() = ip.delta
        set(delta) = notifyIp { ip.delta = delta }

    override fun step() {
        val instr = funge[ip.pos]

        when (mode) {
            PointerMode.Terminated -> return
            PointerMode.String -> when (instr.char) {
                null -> Unit

                '"' -> mode = PointerMode.Normal

                else -> push(instr)
            }
            PointerMode.Normal -> when (instr.char) {
                null -> Unit
                in "0123456789abcdef" -> push(LongData(instr.char.toString().toLong(16)))

                '+' -> argPop(2) { (b, a) -> push(a + b) }
                '-' -> argPop(2) { (b, a) -> push(a - b) }
                '*' -> argPop(2) { (b, a) -> push(a * b) }
                '/' -> argPop(2) { (b, a) -> push(a / b) }
                '%' -> argPop(2) { (b, a) -> push(a mod b) }
                '!' -> argPop(1) { (n) -> push(LongData(if (n.data == 0L) 1L else 0L)) }
                '`' -> argPop(2) { (b, a) -> push(LongData(if (a.data > b.data) 1L else 0L)) }

                '>' -> delta = Vec2.RIGHT
                '<' -> delta = Vec2.LEFT
                'v' -> delta = Vec2.DOWN
                '^' -> delta = Vec2.UP
                '?' -> delta = Vec2.DIRS.chooseOne()
                '#' -> move()

                '_' -> argPop(1) { (n) -> delta = if (n.data == 0L) Vec2.RIGHT else Vec2.LEFT }
                '|' -> argPop(1) { (n) -> delta = if (n.data == 0L) Vec2.DOWN else Vec2.UP }

                '"' -> mode = PointerMode.String

                ':' -> argPop(1) { (n) -> push(n, n) }
                '\\' -> argPop(2) { (b, a) -> push(b, a) }
                '$' -> pop()

                '.' -> argPop(1) { (n) -> stdout.write("${n.data} ") }
                ',' -> argPop(1) { (ch) -> stdout.write(ch.data.toChar().toString()) }

                'p' -> argPop(3) { (y, x, n) -> funge[Vec2(x.data.toInt(), y.data.toInt())] = n }
                'g' -> argPop(2) { (y, x) -> push(funge[Vec2(x.data.toInt(), y.data.toInt())]) }

                '~' -> push(LongData(stdin.read().toLong()))
                '&' -> {
                    val chars = generateSequence {
                        stdin.read().takeIf { it.toChar().isDigit() }
                    }
                    val long = chars.joinToString("").toLong()
                    push(LongData(long))
                }

                '@' -> mode = PointerMode.Terminated
            }
        }

        move()
    }

    override fun reset() {
        stdin.readAll()
        stdout.flush()
    }
}

fun main(args: Array<String>) {
    val stdinSrc = PipedWriter()
    val stdoutDest = PipedReader()

    val int = Interpreter93(stdinSrc, stdoutDest)

//    int.onIpChange += {
//        println(it.to.pos)
//    }

    int.funge.src = """2>:3g" "-!v\  g30          <
 |!`"O":+1_:.:03p>03g+:"O"`|
 @               ^  p3\" ":<
2 234567890123456789012345678901234567890123456789012345678901234567890123456789"""

    while (int.ip.mode != PointerMode.Terminated) {
        int.step()
        print(stdoutDest.readAll())
    }

}