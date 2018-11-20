package befide.ide

import befide.befunge.core.Interpreter
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue

class IOView(val interp: Interpreter) : View() {
    val outputProperty = SimpleStringProperty("")
    var output by outputProperty
    val oldinputProperty = SimpleStringProperty("")
    var oldinput by oldinputProperty


    init {
        interp.outputChanged += {
            while (!interp.stdOutput.isEmpty()) {
                output += interp.stdOutput.remove()
            }
        }
        // add listeners to interp, handle streams, idk
    }

    override val root = vbox {
        addClass("")
        textarea(outputProperty) {
            isWrapText = true
            isEditable = false
            // width, height, idk
            prefHeight = 200.0
        }
        textarea(oldinputProperty) {
            isEditable = false
            prefHeight = 0.0
        }
        textfield {
            onAction = EventHandler {
                oldinput += this.text + '\n'
                this.text.forEach { c -> interp.stdInput.add(c) }
                this.clear()
            }
        }
    }

    fun reset() {
        output = ""
        oldinput = ""
    }
}