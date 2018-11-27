package befide.befunge.b93.state

import befide.befunge.core.state.MutableFunge

class Funge93
    : MutableFunge<Vec2, Data93>() {
    companion object {
        val DEFAULT_DATA = Data93.SPACE
        const val DEFAULT_CHAR = '\u2022' // bullet
    }

    override val size = Vec2(80, 25)

    override fun next(pos: Vec2, delta: Vec2): Vec2 = (pos + delta) mod size

    val contents = hashMapOf<Vec2, Data93>()

    override fun get(pos: Vec2): Data93 = contents[pos] ?: DEFAULT_DATA
    override fun set(pos: Vec2, data: Data93) {
        if (pos in Vec2.ZERO until size) notify(pos) {
            contents[pos] = data
        }
    }

    override var data: Map<Vec2, Data93>
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
            for ((y, row) in data.entries.groupBy { it.key.y }.toSortedMap()) {
                lines += List(y - lines.size) { "" }

                val chars = mutableListOf<Char>()
                for ((v, ch) in row.sortedBy { it.key.x }) {
                    chars += List(v.x - chars.size) { ' ' }
                    chars += ch.char ?: DEFAULT_CHAR
                }
                lines += chars.joinToString("")
            }
            return lines.joinToString("\n")
        }
        set(src) {
            data = src.lines().mapIndexed { y, line ->
                line.mapIndexed { x, ch ->
                    Pair(Vec2(x, y), Data93(ch))
                }
            }.flatten().toMap()
        }

    override fun clear() {
        contents.clear()
    }
}