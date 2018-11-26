package befide.ide

import befide.befunge.b93.padEnd
import befide.befunge.core.Interpreter
import befide.befunge.state.Value
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue

class StackView(val interp: Interpreter) : View() {
    val charOutProperty = SimpleStringProperty()
    var charOut by charOutProperty

    override val root = textarea(charOutProperty) {
        addClass("stack-pane")
        prefWidth = 150.0
        isEditable = false
    }

    private fun <T> getStackStr(mapping: (Value) -> T): String {
        val num = interp.funge.height - 3
        return interp.stack.takeLast(num)
                .map(mapping)
                .padEnd(num, "")
//                .reversed()
                .joinToString("\n")
    }

    init {
        charOut = getStackStr { "${it.asChar ?: '\u2022'} (${it.value})" }

        interp.stackChanged += { _ ->
            charOut = getStackStr { "${it.asChar ?: '\u2022'} (${it.value})" }
        }
    }
}
