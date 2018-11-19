package befide.ide

import befide.befunge.core.Interpreter
import tornadofx.*

class StackView(val interp: Interpreter) : View() {
    override val root = textarea {
        prefWidth = 100.0
    }
}