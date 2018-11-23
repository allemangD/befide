package befide.ide

import befide.befunge.b93.padEnd
import befide.befunge.core.Interpreter
import befide.befunge.state.Value
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue

class StackView(val interp: Interpreter) : View() {
    val charOutProperty = SimpleStringProperty()
    var charOut by charOutProperty
    val longOutProperty = SimpleStringProperty()
    var longOut by longOutProperty


    override val root = hbox {
        textarea(charOutProperty) {
            maxWidth = 1.0
            paddingHorizontal = 1
            isEditable = false
        }
        textarea(longOutProperty) {
            prefWidth = 90.0
            isEditable = false
        }
    }

    private fun <T> getStackStr(mapping: (Value) -> T): String {
        return interp.stack.takeLast(interp.funge.height)
                .map (mapping)
                .padEnd(interp.funge.height, "")
                .reversed()
                .joinToString("\n")
    }

    init {
        charOut = getStackStr { it.asChar ?: '*'}
        longOut = getStackStr { it.value }

        interp.stackChanged += {
            charOut = getStackStr { it.asChar ?: '*'}
            longOut = getStackStr { it.value }
        }
    }
}
