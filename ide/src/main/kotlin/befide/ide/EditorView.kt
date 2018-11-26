package befide.ide

import befide.befunge.b93.B93Interpreter
import befide.befunge.core.Interpreter
import tornadofx.*

class EditorView : View("Befide") {
    private var interp: Interpreter = B93Interpreter()

    private val codeView = CodeView(interp)
    private val stackView = StackView(interp)
    private val ioView = IOView(interp)
    private val actionView = ActionView(interp, codeView, ioView, this)

    override val root = borderpane {
        top { add(actionView) }

        center { add(codeView) }

        right { add(stackView) }

        bottom { add(ioView) }
    }

    init {
        primaryStage.isResizable = false
    }

    override fun onDock() {
        super.onDock()

        codeView.root.requestFocus()
    }
}