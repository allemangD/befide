package befide.ide

import befide.befunge.core.Interpreter
import befide.befunge.state.IpMode
import javafx.animation.Animation
import javafx.animation.Timeline
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.stage.FileChooser
import javafx.util.Duration
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import java.io.File

class ActionView(val interp: Interpreter, val codeView: CodeView, val ioView: IOView) : View() {
    val stepProperty = SimpleBooleanProperty(false)
    var step by stepProperty

    var runTimeline: Timeline = timeline(false) {
        keyframe(Duration.seconds(0.0)) {
            setOnFinished {
                step = interp.step()
            }
        }
        keyframe(Duration.seconds(1.0)) {}

        cycleCount = Animation.INDEFINITE
    }

    val isRunningProperty = SimpleBooleanProperty(false)
    var isRunning by isRunningProperty

    val canResetProperty = SimpleBooleanProperty(false)
    var canReset by canResetProperty

    val saveFileProperty = SimpleObjectProperty<File>()
    var saveFile by saveFileProperty

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

        step = interp.step()
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

    fun save() {
        if (saveFile == null) {
            saveAs()
        } else {
            saveFile.writeText(codeView.src)
        }
    }

    fun saveAs() {
        val fc = FileChooser()
        fc.title = "Save Befunge File"

        val file: File? = fc.showSaveDialog(primaryStage)
        if (file != null) {
            saveFile = file

            save()
        }
    }

    fun open() {
        val fc = FileChooser()
        fc.title = "Open Befunge File"

        val file: File? = fc.showOpenDialog(primaryStage)
        if (file != null) {
            saveFile = file

            reset()
            codeView.src = saveFile.readText()
        }
    }

    override val root = hbox {
        button("step") {
            setOnAction { step() }
            disableWhen(isRunningProperty)
        }

        separator {}

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

        spacer {}

        button("reset") {
            setOnAction { reset() }
            enableWhen(canResetProperty)
        }

        separator {}

        button("open") {
            setOnAction { open() }
            disableWhen(isRunningProperty)
        }

        button("save as") {
            setOnAction { saveAs() }
        }

        button("save") {
            setOnAction { save() }

            enableWhen(saveFileProperty.isNotNull.and(isRunningProperty.not()))
        }

        button("new") {
            setOnAction {
                clearCode()
                saveFile = null
            }
            disableWhen(isRunningProperty)
        }
    }

    init {
        stepProperty.onChange {
            if (!it) stop()
        }
    }
}