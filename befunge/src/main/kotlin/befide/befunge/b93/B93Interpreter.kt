package befide.befunge.b93

import befide.befunge.core.Interpreter
import befide.befunge.events.*
import befide.befunge.state.IpMode
import befide.befunge.state.Value
import befide.befunge.state.Vec
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class B93Interpreter : Interpreter {
    private companion object {
        val RIGHT = Vec(1, 0)
        val LEFT = Vec(-1, 0)
        val UP = Vec(0, -1)
        val DOWN = Vec(0,1)
    }

    override val funge = B93Funge()
    override val stack = Stack<Value>()
    override val ip = B93Pointer(Vec(0, 0), Vec(1, 0), IpMode.Normal)

    override val fungeChanged: Event<FungeEvent> = Event()
    override val stackChanged: Event<StackEvent> = Event()
    override val ipChanged: Event<IpEvent> = Event()
    override val outputChanged: Event<OutputEvent> = Event()

    override val stdInput = LinkedList<Char>()
    override val stdOutput = LinkedList<Char>()

    private val fungeMods = HashMap<Vec,Value>()

    private fun _pop(): Value? {
        if (!stack.empty())
            return stack.pop()
        return null
    }

    private fun pop(): Value {
        val v = _pop()
        val ret = v?.let {
            stackChanged(StackEvent(StackAction.Pop, listOf(it)))
            it
        } ?: Value(0)
        return ret
    }

    private fun pop(num: Int): List<Value> {
        val vs = List(num) {_pop()}
        stackChanged(StackEvent(StackAction.Pop, vs.filterNotNull()))
        return vs.map { it ?: Value(0) }
    }

    private fun _push(v: Value) {
        stack.push(v)
    }

    private fun push(v: Value) {
        _push(v)
        stackChanged.invoke(StackEvent(StackAction.Push, listOf(v)))
    }

    private fun push(v: Value, vararg vs: Value) {
        _push(v)
        vs.forEach { _push(it) }
        stackChanged.invoke(StackEvent(StackAction.Push, listOf(v) + vs.toList()))
    }

    private fun peek(): Value {
        if (!stack.empty()) {
            return stack.peek()
        }
        return Value(0)
    }

    private fun binop(bop: Char): Boolean {
        val (vb, va) = pop(2)
        val a = va.value
        val b = vb.value
        val res = when(bop) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            '%' -> a % b
            '`' -> if (a > b) 1L else 0L
            else -> null
        }
        return res?.let {
            val vres = Value(res)
            push(vres)
        } != null
    }

    private fun unop(uop: Char): Boolean {
        val vv = pop()
        val v = vv.value
        val res = when(uop) {
            '!' -> if (v == 0L) 1L else 0L
            else -> null
        }
        return res?.let {
            val vres = Value(it)
            push(vres)
        } != null
    }

    private fun changeDir(dir: Char): Boolean {
        val newDelta = when(dir) {
            '>' -> RIGHT
            '<' -> LEFT
            '^' -> UP
            'v' -> DOWN
            else -> null
        }
        return newDelta?.let {
            ip.delta = it
        } != null
    }

    private fun randomDir(): Boolean {
        val dirs = listOf('<', '>', '^', 'v')
        val ind = Random().nextInt(4)
        changeDir(dirs[ind])
        return true
    }

    private fun conditional(cop: Char): Boolean {
        val vcond = pop()
        val cond = vcond.value == 0L
        val newDelta = when(cop) {
            '|' -> if (cond) DOWN else UP
            '_' -> if (cond) RIGHT else LEFT
            else -> null
        }
        return newDelta?.let {
            ip.delta = it
        } != null
    }

    private fun toggleStrmode(): Boolean {
        val newMode = when (ip.mode) {
            IpMode.String -> IpMode.Normal
            IpMode.Normal -> IpMode.String
            IpMode.Inactive -> IpMode.Inactive
        }
        ip.mode = newMode
        return true
    }

    private fun stackop(sop: Char): Boolean {
        var ret = true
        when (sop) {
            ':' -> {
                val vc = peek().copy()
                push(vc)
            }
            '\\' -> {
                val (v2, v1) = pop(2)
                push(v2, v1)
            }
            '$' -> {
                pop()
            }
            else -> {
                ret = false
            }
        }
        return ret
    }

    private fun output(type: Char): Boolean {
        val vv = pop()
        val v = vv.value
        val out = when (type) {
            '.' -> v.toString().toList() + ' '
            ',' -> listOf(v.toChar())
            else -> listOf()
        }
        stdOutput.addAll(out)
        outputChanged.invoke(OutputEvent())
        return true
    }

    private fun stepIP(): Boolean {
        ip.pos = funge.nextVec(ip.pos, ip.delta)
        return true
        // No ipChanged here, shown in execInstr
    }

    private fun input(type: Char): Boolean {
        fun <T> Queue<T>.takeWhile(predicate: (T) -> Boolean): Sequence<T?> {
            return sequence {
                if (peek() == null) {
                    yield(null)
                }
                else {
                    while (peek()?.let(predicate) == true) {
                        yield(remove())
                    }
                }
                yield(null)
            }
        }

        val zer = '0'.toLong()
        val inp = when (type) {
            '&' -> {
                val nums = stdInput.takeWhile { it in '0' until '9' }
                val first = nums.first()
                first?.let {
                    nums.filterNotNull()
                            .map {c -> c.toLong() - zer}
                            .fold(it.toLong()-zer) { curr: Long, next: Long ->
                                curr * 10 + next
                            }
                }
            }
            '~' -> stdInput.poll()?.toLong()
            else -> null
        }
        return inp?.let {
            push(Value(it))
        } != null
    }

    private fun fget(): Boolean {
        val (vy, vx) = pop(2)
        val x = vx.value.toInt()
        val y = vy.value.toInt()
        if (0 <= x && x < funge.width && 0 <= y && y <= funge.height) {
            val vv = funge[Vec(x, y)]
            push(vv)
            return true
        }
        return false
    }

    private fun fput(): Boolean {
        val (vy, vx, vv) = pop(3)
        val x = vx.value.toInt()
        val y = vy.value.toInt()
        if (0 <= x && x < funge.width && 0 <= y && y <= funge.height) {
            val loc = Vec(x, y)
            val old = funge[loc]
            funge[loc] = vv
            if (!fungeMods.contains(loc)) {
                fungeMods[loc] = old
            }
            fungeChanged(FungeEvent(listOf(FungeChange(loc,old,vv))))
            return true
        }
        return false
    }

    private fun terminate(): Boolean {
        ip.mode = IpMode.Inactive
        return true
    }

    private fun pushDig(dig: Char): Boolean {
        val num = dig.toLong() - '0'.toLong()
        push(Value(num))
        return true
    }

    private fun noOp(): Boolean = false

    private fun execInstr(instr: Value): Boolean {
        val car = instr.asChar
        return when (car) {
            null -> noOp()
            '+', '-', '*', '/', '%', '`' -> binop(car)
            '!' -> unop(car)
            '>', '<', '^', 'v' -> changeDir(car)
            '?' -> randomDir()
            '_', '|' -> conditional(car)
            '"' -> toggleStrmode()
            ':', '\\', '$' -> stackop(car)
            '.', ',' -> output(car)
            '#' -> stepIP()
            'g' -> fget()
            'p' -> fput()
            '&', '~' -> input(car)
            '@' -> terminate()
            in '0'..'9' -> pushDig(car)
            else -> noOp()
        }
    }

    private fun strMode(instr: Value): Boolean {
        if (instr.asChar == '"') {
            toggleStrmode()
            return true
        }
        push(instr)
        return true
    }

    override fun step(): Boolean {
        val instr = funge[ip.pos]
        val currIP = ip.copy()
        val processed = when (ip.mode) {
            IpMode.Inactive -> noOp()
            IpMode.Normal -> execInstr(instr)
            IpMode.String -> strMode(instr)
        }
        if (processed) {
            stepIP()
            if (ip.mode != IpMode.String) {
                while (funge[ip.pos].asChar == ' ') {
                    stepIP()
                }
            }
            val newIP = ip.copy()
            ipChanged(IpEvent(currIP, newIP))
        }
        return ip.mode != IpMode.Inactive
    }

    override fun reset() {
        val oldIP = ip.copy()
        ip.pos = Vec(0,0)
        ip.delta = RIGHT
        ip.mode = IpMode.Normal
        val newIP = ip.copy()
        ipChanged.invoke(IpEvent(oldIP, newIP))

        stack.clear()
        stackChanged(StackEvent(StackAction.Clear, listOf()))

        stdInput.clear()
        stdOutput.clear()

        val changes = fungeMods.map { FungeChange(it.key, funge[it.key], it.value) }
        for ((k,v) in fungeMods) {
            funge[k] = v
        }
        fungeMods.clear()
        fungeChanged(FungeEvent(changes))
    }
}