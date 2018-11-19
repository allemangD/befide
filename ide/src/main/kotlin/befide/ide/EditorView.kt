package befide.ide

import befide.befunge.b93.B93Interpreter
import befide.befunge.core.Interpreter
import tornadofx.*

class EditorView : View("Befide") {
    private var interp: Interpreter = B93Interpreter()

    private val codeView = CodeView(interp)
    private val actionView = ActionView(interp)
    private val stackView = StackView(interp)
    private val ioView = IOView(interp)

    override val root = borderpane {
        addClass("editor-root")

        top { add(actionView) }

        center {
            add(codeView)
        }

        right {
            add(stackView)
        }

        bottom { add(ioView) }
    }

    init {
        codeView.src = """64+"!dlroW ,olleH">:#,_@"""
    }
}