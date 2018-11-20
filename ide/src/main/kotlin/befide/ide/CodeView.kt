package befide.ide

import befide.befunge.core.Interpreter
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class CodeView(val interp: Interpreter) : View() {
    val srcProperty = SimpleStringProperty("")
    var src by srcProperty

    init {
        interp.fungeChanged += { src = interp.funge.toString() }
        srcProperty.addListener { _, _, newValue -> interp.funge.setString(newValue) }
    }

    override val root = textarea(srcProperty) {
        prefRowCount = 25
        prefColumnCount = 80
    }
}