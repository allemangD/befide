package befide.befunge.b93

import befide.befunge.core.Interpreter
import befide.befunge.core.Pointer
import befide.befunge.events.*
import befide.befunge.state.IpMode
import befide.befunge.state.Value
import befide.befunge.state.Vec
import java.util.*

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

    private val fungeMods = HashMap<Vec,Value>()

    private fun _pop(): Value? {
        if (!stack.empty())
            return stack.pop()
        return null
    }

    private fun popOne(): Value {
        val v = _pop()
        if (v != null) {
            stackChanged(StackEvent(StackAction.Pop, listOf(v)))
        }
        return Value(0)
    }

    private fun popMany(num: Int): List<Value> {
        val vs = List(num) {_pop()}
        stackChanged(StackEvent(StackAction.Pop, vs.filterNotNull()))
        return vs.map { it ?: Value(0) }
    }

    private fun _push(v: Value) {
        stack.push(v)
    }

    private fun pushOne(v: Value) {
        _push(v)
        stackChanged.invoke(StackEvent(StackAction.Push, listOf(v)))
    }

    private fun pushMany(vs: List<Value>) {
        vs.forEach { _push(it) }
        stackChanged.invoke(StackEvent(StackAction.Push, vs))
    }

    private fun binop(bop: Char) {
        val (vb, va) = popMany(2)
        val a = va.value
        val b = vb.value
        val res = when(bop) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            '`' -> if (a > b) 1L else 0L
            else -> null
        }
        if (res != null) {
            val vres = Value(res)
            pushOne(vres)
        }
    }

    private fun unop(uop: Char) {
        val vv = popOne()
        val v = vv.value
        val res = when(uop) {
            '!' -> if (v == 0L) 1L else 0L
            else -> null
        }
        if (res != null) {
            val vres = Value(res)
            pushOne(vres)
        }
    }

    private fun changeDir(dir: Char) {
        val newDelta = when(dir) {
            '>' -> RIGHT
            '<' -> LEFT
            '^' -> UP
            'v' -> DOWN
            else -> null
        }
        if (newDelta != null) {
            ip.delta = newDelta
        }
    }

    private fun randomDir() {
        val dirs = listOf('<', '>', '^', 'v')
        val ind = Random().nextInt(4)
        changeDir(dirs[ind])
    }

    private fun conditional(cop: Char) {
        val vcond = popOne()
        val cond = vcond.value == 0L
        val newDelta = when(cop) {
            '|' -> if (cond) DOWN else UP
            '_' -> if (cond) RIGHT else LEFT
            else -> null
        }
        if (newDelta != null) {
            ip.delta = newDelta
        }
    }

    private fun toggleStrmode() {
        val newMode = when (ip.mode) {
            IpMode.String -> IpMode.Normal
            IpMode.Normal -> IpMode.String
            IpMode.Inactive -> IpMode.Inactive
        }
        ip.mode = newMode
    }

    private fun stackop(sop: Char) {
        when (sop) {
            ':' -> {
                val vc = stack.peek().copy()
                pushOne(vc)
            }
            '\\' -> {
                val (v2, v1) = popMany(2)
                pushMany(listOf(v1, v2))
            }
            '$' -> {
                popOne()
            }
        }
    }

    private fun output(type: Char) {
        val vv = popOne()
        val v = vv.value
        val out = when (type) {
            '.' -> v.toString().toCharArray()
            ',' -> charArrayOf(v.toChar())
            else -> charArrayOf()
        }
        //TODO output {out} chararray
    }

    private fun stepIP() {
        ip.pos = funge.nextVec(ip.pos, ip.delta)
        // No ipChanged here, shown in execInstr
    }

    private fun input() {
        val inp = 0L //TODO get input
        val vinp = Value(inp)
        pushOne(vinp)
    }

    private fun fget() {
        val (vy, vx) = popMany(2)
        val x = vx.value.toInt()
        val y = vy.value.toInt()
        if (0 <= x && x < funge.width && 0 <= y && y <= funge.height) {
            val vv = funge[Vec(x, y)]
            pushOne(vv)
        }
    }

    private fun fput() {
        val (vy, vx, vv) = popMany(3)
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
        }
    }

    private fun terminate() {
        ip.mode = IpMode.Inactive
    }

    private fun noOp() {}

    private fun execInstr(instr: Value) {
        val car = instr.asChar
        when (car) {
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
            '&', '~' -> input()
            '@' -> terminate()
            else -> noOp()
        }
    }

    override fun step(): Boolean {
        val instr = funge[ip.pos]
        val currIP = ip.copy()
        when (ip.mode) {
            IpMode.Inactive -> noOp()
            IpMode.Normal -> execInstr(instr)
            IpMode.String -> pushOne(instr)
        }
        if (ip.mode != IpMode.Inactive) {
            stepIP()
            while (funge[ip.pos].asChar == ' ') {
                stepIP()
            }
            val newIP = ip.copy()
            ipChanged(IpEvent(currIP, newIP))
        }
        return true
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

        val changes = fungeMods.map { FungeChange(it.key, funge[it.key], it.value) }
        for ((k,v) in fungeMods) {
            funge[k] = v
        }
        fungeChanged(FungeEvent(changes))
    }
}