package befide.ide

import befide.befunge.core.Interpreter
import javafx.animation.Animation
import javafx.animation.Timeline
import javafx.util.Duration
import tornadofx.*

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

    override val root = hbox {
        button("step") {
            setOnAction {
                interp.funge.values = codeView.values

                interp.step()
            }
        }

        button("reset") {
            setOnAction {
                interp.reset()
                ioView.reset()

                codeView.values = interp.funge.values
            }
        }

        button("run") {
            setOnAction {
                interp.funge.values = codeView.values

                runTimeline.rate = 10000.0
                runTimeline.playFromStart()
            }
        }

        button("walk") {
            setOnAction {
                interp.funge.values = codeView.values

                runTimeline.rate = 50.0
                runTimeline.playFromStart()
            }
        }

        button("crawl") {
            setOnAction {
                interp.funge.values = codeView.values

                runTimeline.rate = 4.0
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