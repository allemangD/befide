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
                interp.funge.setString(codeView.src)
                interp.step()
            }
        }

        button("reset") {
            enableWhen(codeView.lockedProperty)

            setOnAction {
                interp.reset()
                ioView.reset()
                codeView.src = interp.funge.toString()
                codeView.locked = false
            }
        }

        button("run") {
            setOnAction {
                interp.funge.setString(codeView.src)
                codeView.locked = true
                runTimeline.rate = 10000.0
                runTimeline.playFromStart()
            }
        }

        button("walk") {
            setOnAction {
                interp.funge.setString(codeView.src)
                codeView.locked = true
                runTimeline.rate = 50.0
                runTimeline.playFromStart()
            }
        }

        button("crawl") {
            setOnAction {
                interp.funge.setString(codeView.src)
                codeView.locked = true
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