package befide.ide

import befide.befunge.core.Interpreter
import befide.befunge.state.Vec
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Label
import tornadofx.*

class CodeLabel(val pos: Vec, val cursorPos: ObjectProperty<Vec>, val interp: Interpreter) : Label() {
    val charProperty = SimpleObjectProperty<Char>('\u0000')
    var char: Char by charProperty

    fun restyle() {
        styleClass.setAll("code")

        if (char in "0123456789") styleClass.add("code-num")
        if (char in "gp") styleClass.add("code-funge")
        if (char in "<>^v?#") styleClass.add("code-dir")

        if (pos == cursorPos.value) styleClass.add("code-cursor")
        if (pos == interp.ip.pos) styleClass.add("code-cursor-ip")
        if (char == '\u2022') styleClass.add("unknown")
    }

    init {
        textProperty().bind(charProperty.stringBinding { it?.toString() ?: " " })

        setOnMouseClicked {
            cursorPos.value = pos
        }

        charProperty.addListener { _, _, _ -> restyle() }

        char = ' '
    }
}