package befide.ide

import befide.befunge.b93.B93Interpreter
import befide.befunge.core.Interpreter
import befide.befunge.core.Pointer
import javafx.animation.Animation
import javafx.animation.Timeline
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.util.Duration
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue

class IOView(val interp: Interpreter) : View() {
    val textProperty = SimpleStringProperty("")
    var text by textProperty

    init {
        // add listeners to interp, handle streams, idk
    }

    override val root = vbox {
        textarea(textProperty) {
            // width, height, idk
            prefHeight = 200.0
        }
        textfield {

        }
    }
}

class ActionView(val interp: Interpreter) : View() {
    var runTimeline: Timeline = timeline(false) {
        keyframe(Duration.seconds(0.0)) {
            setOnFinished {
                if (!interp.step())
                    this@timeline.stop()
            }
        }
        keyframe(Duration.seconds(1.0)) {}

        cycleCount = Animation.INDEFINITE
    }

    override val root = hbox {
        button("step") {
            setOnAction { interp.step() }
        }

        button("reset") {
            setOnAction { interp.reset() }
        }

        button("run") {
            setOnAction {
                runTimeline.rate = 1000.0
                runTimeline.playFromStart()
            }
        }

        button("crawl") {
            setOnAction {
                runTimeline.rate = 2.0
                runTimeline.playFromStart()
            }
        }

        button("stop") {
            setOnAction {
                runTimeline.stop()
            }
        }
    }
}

class CodeView(val interp: Interpreter) : View() {
    val srcProperty = SimpleStringProperty("")
    var src by srcProperty

    init {
        interp.fungeChanged += { src = interp.funge.toString() }
        srcProperty.addListener { _, _, newValue -> interp.funge.setString(newValue) }
    }

    override val root = textarea(srcProperty) {

    }
}

class StackView(val interp: Interpreter) : View() {
    override val root = textarea {
        prefWidth = 100.0
    }
}

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

class MainApp : App(EditorView::class) {
    init {
        importStylesheet(resources["style.css"])
    }
}
