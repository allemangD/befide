package befide.ide

import befide.befunge.core.Interpreter
import befide.befunge.state.Value
import befide.befunge.state.Vec
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCode
import tornadofx.*

operator fun <T> List<List<T>>.get(v: Vec): T = this[v.y][v.x]

class CodeView(val interp: Interpreter) : View() {
    val cursorPosProperty: ObjectProperty<Vec> = SimpleObjectProperty<Vec>(Vec(0, 0))
    var cursorPos by cursorPosProperty

    val cursorDeltaProperty = SimpleObjectProperty<Vec>(Vec(1, 0))
    var cursorDelta by cursorDeltaProperty

    var labels: List<List<CodeLabel>> = List(25) { y -> List(80) { x -> CodeLabel(Vec(x, y), cursorPosProperty, interp) } }

    var values: List<List<Value>>
        get() = labels.map { it.map { it.value } }
        set(data) {
            for (y in 0 until labels.size)
                for (x in 0 until labels[y].size)
                    labels[y][x].value = data[y][x]
        }

    init {
        cursorPosProperty.addListener { _, old, new ->
            labels[old].restyle()
            labels[new].restyle()
        }

        interp.ipChanged += {
            labels[it.from.pos].restyle()
            labels[it.to.pos].restyle()
        }

        interp.fungeChanged += {
            for (change in it.changes) {
                labels[change.vec].value = change.to
                labels[change.vec].restyle()
            }
        }
    }

    fun move(delta: Vec? = null) {
        cursorPos = interp.funge.nextVec(cursorPos, delta ?: cursorDelta)
    }

    var src: String
        get() = labels.joinToString("\n") { row ->
            row.dropLastWhile { lbl ->
                lbl.value.asChar?.isWhitespace() ?: false
            }.joinToString("") { lbl ->
                lbl.value.asChar?.toString() ?: "\u2022"
            }
        }
        set(value) {
            val lines = value.lines()

            for (row in labels) {
                for (lbl in row) {
                    val char = lines.getOrNull(lbl.pos.y)?.getOrNull(lbl.pos.x) ?: ' '
                    lbl.value = Value(char)
                }
            }
        }

    override val root = hbox {
        isFocusTraversable = true

        addClass("code-view")

        vbox {
            children.setAll(labels.map { row -> hbox { children.setAll(row) } })
        }

        setOnMouseClicked {
            requestFocus()
        }

        setOnKeyPressed {
            when (it.code) {
                KeyCode.RIGHT -> move(Vec(1, 0))
                KeyCode.LEFT -> move(Vec(-1, 0))
                KeyCode.DOWN -> move(Vec(0, 1))
                KeyCode.UP -> move(Vec(0, -1))
                else -> return@setOnKeyPressed
            }
            if (it.isAltDown) when (it.code) {
                KeyCode.RIGHT -> cursorDelta = Vec(1, 0)
                KeyCode.LEFT -> cursorDelta = Vec(-1, 0)
                KeyCode.DOWN -> cursorDelta = Vec(0, 1)
                KeyCode.UP -> cursorDelta = Vec(0, -1)
                else -> Unit
            }
            it.consume()
        }

        setOnKeyTyped {
            for (ch in it.character) {
                when {
                    !ch.isISOControl() -> {
                        cursorDelta = when (ch) {
                            '>' -> Vec(1, 0)
                            '<' -> Vec(-1, 0)
                            'v' -> Vec(0, 1)
                            '^' -> Vec(0, -1)
                            else -> cursorDelta
                        }

                        labels[cursorPos].value = Value(ch)

                        move()
                    }

                    ch == '\u0008' -> {  // backspace
                        move(-cursorDelta)

                        labels[cursorPos].value = Value(' ')
                    }

                    else -> {
                        println("'$ch' (${ch.toInt()})")
                    }
                }
            }
        }
    }
}