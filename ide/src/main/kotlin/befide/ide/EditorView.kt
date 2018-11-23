package befide.ide

import befide.befunge.b93.B93Interpreter
import befide.befunge.core.Interpreter
import tornadofx.*

class EditorView : View("Befide") {
    private var interp: Interpreter = B93Interpreter()

    private val codeView = CodeView(interp)
    private val stackView = StackView(interp)
    private val ioView = IOView(interp)
    private val actionView = ActionView(interp, ioView)

    override val root = borderpane {
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
        codeView.src = """>84*>:#v_55+"ude.ub@yelruta">:#,_@>188*+>\02p\12p\:22p#v_${'$'}    55+,1-         v
    ^  0 v +1\                   _^#-+*<               >22g02g*"_@"*-!1- #v_v>
       >:>::3g: ,\188                  ^^               -1\g21\g22<p3\"_":<
________________________________@_________________________________^  p3\"@":<"""
//        codeView.src = """--------------------------------------------------------------------------------
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//            |-
//        """.trimMargin()
    }
}