package befide.ide

import befide.befunge.core.Interpreter
import javafx.animation.Animation
import javafx.animation.Timeline
import javafx.util.Duration
import tornadofx.*

class ActionView(val interp: Interpreter, val ioView: IOView) : View() {
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
            setOnAction {
                interp.reset()
                ioView.reset()
            }
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