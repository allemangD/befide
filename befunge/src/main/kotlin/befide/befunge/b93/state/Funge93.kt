package befide.befunge.b93.state

import befide.befunge.core.events.FungeChange
import befide.befunge.core.state.MutableFunge
import befide.befunge.core.util.Event

class Funge93
    : MutableFunge<Vec2, LongData> {
    override val size = Vec2(80, 25)

    override val onChange = Event<FungeChange<Vec2, LongData>>()

    override fun next(pos: Vec2, delta: Vec2): Vec2 = (pos + delta) mod size

    override fun defaultData(): LongData = LongData.SPACE
    override fun defaultChar(): Char = '\u2022' // bullet

    val contents = hashMapOf<Vec2, LongData>()

    override fun get(pos: Vec2): LongData = contents[pos] ?: defaultData()
    override fun set(pos: Vec2, data: LongData) {
        if (pos.x !in 0 until size.x || pos.y !in 0 until size.y)
            return

        val from = this[pos]
        contents[pos] = data
        onChange(FungeChange(this, pos, from, data))
    }

    override var data: Map<Vec2, LongData>
        get() {
            return contents.toMap()
        }
        set(data) {
            clear()
            for ((v, d) in data.entries)
                this[v] = d
        }

    override var src: String
        get() {
            val lines = mutableListOf<String>()
            for ((y, row) in contents.entries.groupBy { it.key.y }.toSortedMap()) {
                lines += List(y - lines.size) { "" }

                val chars = mutableListOf<Char>()
                for ((v, ch) in row.sortedBy { it.key.x }) {
                    chars += List(v.x - chars.size) { ' ' }
                    chars += ch.char ?: defaultChar()
                }
                lines += chars.joinToString("")
            }
            return lines.joinToString("\n")
        }
        set(src) {
            data = src.lines().mapIndexed { y, line ->
                line.mapIndexed { x, ch ->
                    Pair(Vec2(x, y), LongData(ch))
                }
            }.flatten().toMap()
        }

    override fun clear() {
        contents.clear()
    }
}