package befide.ide

import befide.befunge.b93.B93Interpreter
import befide.befunge.core.Interpreter
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.Parent
import javafx.scene.control.Label
import tornadofx.*


class SampleView : View() {
    private val controller: SampleController by inject()

    override val root: Parent by fxml()

    private val width: Label by fxid()
    private val height: Label by fxid()

    init {
        importStylesheet(resources["style.css"])

        width.bind(controller.width)
        height.bind(controller.height)
    }
}

class SampleController : Controller() {
    private val bf: Interpreter = B93Interpreter()

    val width = SimpleIntegerProperty()
    val height = SimpleIntegerProperty()

    init {
        bf.fungeChanged += { updateFungeLabels() }

        updateFungeLabels()
    }

    fun updateFungeLabels() {
        width.set(bf.funge.width)
        height.set(bf.funge.height)
    }
}

class SampleApp : App() {
    override val primaryView = SampleView::class
}

fun main(args: Array<String>) {
    launch<SampleApp>(args)
}