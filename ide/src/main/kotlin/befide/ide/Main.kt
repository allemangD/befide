package befide.ide

import tornadofx.*

class MainApp : App(EditorView::class) {
    init {
        importStylesheet(resources["style.css"])
    }
}

fun main(args: Array<String>) {
    launch<MainApp>(args)
}