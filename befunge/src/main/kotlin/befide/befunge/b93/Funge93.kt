package befide.befunge.b93

import befide.befunge.core.FungeChange
import befide.befunge.core.MutableFunge
import befide.befunge.events.Event

class Funge93
    : MutableFunge<Vec2, LongData> {
    override val size = Vec2(80, 25)

    override val onChange = Event<FungeChange<Vec2, LongData>>()

    override fun next(pos: Vec2, delta: Vec2): Vec2 = (pos + delta) mod size

    val contents = Array(size.y) { Array(size.x) { LongData.SPACE } }

    override fun get(pos: Vec2): LongData = contents[pos.y][pos.x]
    override fun set(pos: Vec2, data: LongData) {
        val from = this[pos]
        contents[pos.y][pos.x] = data
        onChange(FungeChange(this, pos, from, data))
    }

    override var data: List<List<LongData>>
        get() {
            return contents.map(Array<LongData>::toList)
        }
        set(data) {
            for (y in 0 until size.y) for (x in 0 until size.x) {
                val pos = Vec2(x, y)
                val new = data.getOrNull(y)?.getOrNull(x) ?: this[pos]
                this[pos] = new
            }
        }

    override var src: String
        get() {
            return contents.map { row ->
                row.dropLastWhile { ch ->
                    ch.char?.isISOControl() ?: false
                }.map { ch ->
                    ch.char ?: '\u2022' // bullet
                }.joinToString("")
            }.dropLastWhile { line ->
                line.isEmpty()
            }.joinToString("\n")
        }
        set(data) {
            val lines = data.lines()
            for (y in 0 until size.y) for (x in 0 until size.x) {
                val pos = Vec2(x, y)
                val new = lines.getOrNull(y)?.getOrNull(x)?.let(::LongData) ?: this[pos]
                this[pos] = new
            }
        }
}