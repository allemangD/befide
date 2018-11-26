package befide.ide

import befide.befunge.core.Interpreter
import befide.befunge.state.Value
import befide.befunge.state.Vec
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Label
import tornadofx.*

class CodeLabel(val pos: Vec, val cursorPos: ObjectProperty<Vec>, val interp: Interpreter) : Label() {
    var valueProperty = SimpleObjectProperty<Value>(Value(' '))
    var value: Value by valueProperty

    fun restyle() {
        styleClass.setAll("code")

        val char = value.asChar ?: '\u2022'

        if (char in "0123456789") styleClass.add("code-num")
        if (char in "gp") styleClass.add("code-funge")
        if (char in "<>^v?#") styleClass.add("code-dir")
        if (char in "@") styleClass.add("code-stop")
        if (char in "_|") styleClass.add("code-condition")
        if (char in "\"") styleClass.add("code-quote")

        if (pos == cursorPos.value) styleClass.add("code-cursor")
        if (pos == interp.ip.pos) styleClass.add("code-cursor-ip")
        if (char == '\u2022') styleClass.add("unknown")
    }

    init {
        textProperty().bind(valueProperty.stringBinding { it?.asChar?.toString() ?: "\u2022" })

        setOnMouseClicked {
            cursorPos.value = pos
        }

        valueProperty.addListener { _, _, _ -> restyle() }

        restyle()
    }
}