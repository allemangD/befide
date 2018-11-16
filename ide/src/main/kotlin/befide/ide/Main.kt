package befide.ide

import befide.befunge.b93.B93Interpreter
import befide.befunge.core.Interpreter
import befide.befunge.state.Vec
import javafx.animation.Animation
import javafx.animation.Timeline
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.util.Duration
import tornadofx.*

class EditorView : View("Befide") {
    private var interp: Interpreter = B93Interpreter()

    private val textProperty = SimpleStringProperty("")
    private var text by textProperty

    private val posProperty = SimpleObjectProperty<Vec>(Vec(0, 0))
    private var pos by posProperty

    private var runTimeline: Timeline = timeline(false) {
        keyframe(Duration.seconds(0.0)) {
            setOnFinished {
                if (!interp.step())
                    this@timeline.stop()
            }
        }
        keyframe(Duration.seconds(1.0)) {}

        cycleCount = Animation.INDEFINITE
    }

    init {
        interp.fungeChanged += { text = interp.funge.toString() }
        textProperty.addListener { _, _, newValue -> interp.funge.setString(newValue) }
        interp.ipChanged += { pos = it.to.pos }
    }

    override val root = vbox {
        label {
            bind(posProperty.stringBinding { if (it != null) "${it.x} ${it.y}" else "-" })
        }

        hbox {
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

        textarea(textProperty)
    }

    init {
        text = """64+"!dlroW ,olleH">:#,_@"""
    }
}

class MainApp : App(EditorView::class) {
    init {
        importStylesheet(resources["style.css"])
    }
}
