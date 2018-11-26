package befide.ide

import befide.befunge.core.Interpreter
import befide.befunge.state.Vec
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCode
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue

operator fun <T> List<List<T>>.get(v: Vec): T = this[v.y][v.x]

class CodeView(val interp: Interpreter) : View() {
    val cursorPosProperty: ObjectProperty<Vec> = SimpleObjectProperty<Vec>(Vec(0, 0))
    var cursorPos by cursorPosProperty

    val cursorDeltaProperty = SimpleObjectProperty<Vec>(Vec(1, 0))
    var cursorDelta by cursorDeltaProperty

    val lockedProperty = SimpleBooleanProperty(false)
    var locked by lockedProperty

    var labels: List<List<CodeLabel>> = List(25) { y -> List(80) { x -> CodeLabel(Vec(x, y), cursorPosProperty, interp) } }

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
                labels[change.vec].char = change.to.asChar ?: '\u2022'
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
                lbl.char.isWhitespace()
            }.joinToString("") { lbl ->
                lbl.char.toString()
            }
        }
        set(value) {
            val lines = value.lines()

            for (row in labels) {
                for (lbl in row) {
                    lbl.char = lines.getOrNull(lbl.pos.y)?.getOrNull(lbl.pos.x) ?: ' '
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
            if (locked) return@setOnKeyTyped

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

                        labels[cursorPos].char = ch

                        move()
                    }

                    ch == '\u0008' -> {  // backspace
                        move(-cursorDelta)

                        labels[cursorPos].char = ' '
                    }

                    else -> {
                        println("'$ch' (${ch.toInt()})")
                    }
                }
            }
        }
    }
}