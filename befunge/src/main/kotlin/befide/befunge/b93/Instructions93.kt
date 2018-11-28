package befide.befunge.b93

import befide.befunge.b93.state.Data93
import befide.befunge.b93.state.PointerMode
import befide.befunge.b93.state.Vec2
import befide.befunge.core.InstructionSet
import befide.befunge.core.MutableInterpreter
import befide.befunge.core.util.chooseOne

class B93Instructions : InstructionSet<Vec2, Data93, PointerMode> {
    override fun MutableInterpreter<Vec2, Data93, PointerMode>.handle(): Boolean {
        when (mode) {
            PointerMode.Terminated -> return true
            PointerMode.String -> when (instr.char) {
                null -> return false
                '"' -> mode = PointerMode.Normal
                else -> push(instr)
            }
            PointerMode.Normal -> when (instr.char) {
                null -> return false

                in "0123456789" -> push(Data93(instr.char.toString().toLong(16)))

                '+' -> pop(2).let { (b, a) -> push(a + b) }
                '-' -> pop(2).let { (b, a) -> push(a - b) }
                '*' -> pop(2).let { (b, a) -> push(a * b) }
                '/' -> pop(2).let { (b, a) -> push(a / b) }
                '%' -> pop(2).let { (b, a) -> push(a mod b) }
                '!' -> pop(1).let { (n) -> push(Data93(if (n.data == 0L) 1L else 0L)) }
                '`' -> pop(2).let { (b, a) -> push(Data93(if (a.data > b.data) 1L else 0L)) }

                '>' -> delta = Vec2.RIGHT
                '<' -> delta = Vec2.LEFT
                'v' -> delta = Vec2.DOWN
                '^' -> delta = Vec2.UP
                '?' -> delta = Vec2.DIRS.chooseOne()
                '#' -> move()

                '_' -> pop(1).let { (n) -> delta = if (n.data == 0L) Vec2.RIGHT else Vec2.LEFT }
                '|' -> pop(1).let { (n) -> delta = if (n.data == 0L) Vec2.DOWN else Vec2.UP }

                '"' -> mode = PointerMode.String

                ':' -> pop(1).let { (n) -> push(n, n) }
                '\\' -> pop(2).let { (b, a) -> push(b, a) }
                '$' -> pop(1)

                '.' -> pop(1).let { (n) -> stdout?.write("${n.data} ") }
                ',' -> pop(1).let { (ch) -> stdout?.write(ch.data.toChar().toString()) }

                'p' -> pop(3).let { (y, x, n) -> funge[Vec2(x.data.toInt(), y.data.toInt())] = n }
                'g' -> pop(2).let { (y, x) -> push(funge[Vec2(x.data.toInt(), y.data.toInt())]) }

                '~' -> push(Data93(stdin?.read()?.toLong() ?: 0L))
                '&' -> {
                    val chars = generateSequence {
                        stdin?.read()?.toChar()?.takeIf { it.isDigit() }
                    }
                    val long = chars.joinToString("") {
                        it.toString()
                    }.ifEmpty { "0" }.toLong()
                    push(Data93(long))
                }

                '@' -> mode = PointerMode.Terminated

                ' ' -> do move() while (next.char == ' ')

                else -> return false
            }
        }

        return true
    }
}

class B93Extras : InstructionSet<Vec2, Data93, PointerMode> {
    override fun MutableInterpreter<Vec2, Data93, PointerMode>.handle(): Boolean {
        when (mode) {
            PointerMode.Terminated -> return true
            PointerMode.String -> return false
            PointerMode.Normal -> when (instr.char) {
                null -> return false

                in "0123456789abcdef" -> push(Data93(instr.char.toString().toLong(16)))

                '\'' -> {
                    move()
                    push(instr)
                }

                ';' -> do move() while (instr.char != ';')
            }
        }
        return true
    }
}