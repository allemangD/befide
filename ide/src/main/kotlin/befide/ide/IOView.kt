package befide.ide

import befide.befunge.core.Interpreter
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class IOView(val interp: Interpreter) : View() {
    val outputProperty = SimpleStringProperty("")
    var output by outputProperty

    init {
        // add listeners to interp, handle streams, idk
    }

    override val root = vbox {
        addClass("")
        textarea(outputProperty) {
            // width, height, idk
            prefHeight = 200.0
        }
        textfield {

        }
    }
}