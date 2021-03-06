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

class ActionView(val interp: Interpreter, val codeView: CodeView, val ioView: IOView, val editorView: EditorView) : View() {
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

        if (interp.ip.mode == IpMode.Inactive) reset()

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

    private val chooser: FileChooser = FileChooser().apply {
        title = "Befunge File"
        extensionFilters.setAll(
                FileChooser.ExtensionFilter("Befunge 93", "*.bf", "*.b93"),
                FileChooser.ExtensionFilter("Befunge 98", "*.bf", "*.b98"))
    }

    fun saveAs() {
        val file: File? = chooser.showSaveDialog(primaryStage)
        if (file != null) {
            saveFile = file
            save()
        }
    }

    fun open() {
        val file: File? = chooser.showOpenDialog(primaryStage)
        if (file != null) {
            saveFile = file

            editorView.title = "${saveFile.nameWithoutExtension} [${saveFile.absolutePath}] - Befide"

            reset()
            codeView.src = saveFile.readText()
        }
    }

    fun new() {
        clearCode()
        saveFile = null
        editorView.title = "Befide"
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
            setOnAction { new() }
            disableWhen(isRunningProperty)
        }
    }

    init {
        stepProperty.onChange {
            if (!it) stop()
        }

        new()
    }
}