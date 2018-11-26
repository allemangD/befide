package befide.ide

import befide.befunge.b93.B93Interpreter
import befide.befunge.core.Interpreter
import tornadofx.*

class EditorView : View("Befide") {
    private var interp: Interpreter = B93Interpreter()

    private val codeView = CodeView(interp)
    private val stackView = StackView(interp)
    private val ioView = IOView(interp)
    private val actionView = ActionView(interp, codeView, ioView)

    override val root = borderpane {
        top { add(actionView) }

        center { add(codeView) }

        right { add(stackView) }

        bottom { add(ioView) }
    }

    init {
        codeView.src = """v ;11101010;
>>>>>>>>>>>>>55+0g68*-90g68*-2*+80g68*-4*+70g68*-8*+v
@.+***288-*86g03+**88-*86g04+**84-*86g05+**44-*86g06<"""

//        codeView.src = """ 2>:3g" "-!v\  g30          <
//  |!`"O":+1_:.:03p>03g+:"O"`|
//  @               ^  p3\" ":<
//2 234567890123456789012345678901234567890123456789012345678901234567890123456789"""

//        codeView.src = """"hello world",,,,,,,,,,,@"""
    }

    override fun onDock() {
        super.onDock()

        codeView.root.requestFocus()
    }
}