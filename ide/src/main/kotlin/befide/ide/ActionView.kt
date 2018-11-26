package befide.ide

import befide.befunge.core.Interpreter
import javafx.animation.Animation
import javafx.animation.Timeline
import javafx.beans.property.SimpleBooleanProperty
import javafx.util.Duration
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue

class ActionView(val interp: Interpreter, val codeView: CodeView, val ioView: IOView) : View() {
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

    val isRunningProperty = SimpleBooleanProperty(false)
    var isRunning by isRunningProperty

    val canResetProperty = SimpleBooleanProperty(false)
    var canReset by canResetProperty

    fun start(rate: Double) {
        stop()

        isRunning = true
        canReset = true

        interp.funge.values = codeView.values

        runTimeline.rate = rate
        runTimeline.playFromStart()
    }

    fun step() {
        canReset = true

        interp.funge.values = codeView.values

        interp.step()
    }

    fun stop() {
        isRunning = false

        runTimeline.stop()
    }

    fun reset() {
        stop()
        interp.reset()
        ioView.reset()
        canReset = false
    }

    fun clearCode() {
        reset()
        codeView.clear()
    }

    override val root = hbox {
        button("step") {
            setOnAction { step() }
            disableWhen(isRunningProperty)
        }

        separator { }

        button("run") {
            setOnAction { start(10000.0) }
        }

        button("walk") {
            setOnAction { start(50.0) }
        }

        button("crawl") {
            setOnAction { start(4.0) }
        }

        separator {}

        button("stop") {
            setOnAction { stop() }
            enableWhen(isRunningProperty)
        }

        spacer()

        button("reset") {
            setOnAction { reset() }
            enableWhen(canResetProperty.and(isRunningProperty.not()))
        }

        button("clear") {
            setOnAction { clearCode() }
            disableWhen(isRunningProperty)
        }
    }
}