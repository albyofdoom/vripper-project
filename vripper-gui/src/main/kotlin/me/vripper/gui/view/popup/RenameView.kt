package me.vripper.gui.view.popup

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import me.vripper.gui.controller.PostController
import tornadofx.*

class RenameView : Fragment("Rename download post") {

    val postId: Long by param()
    val name: String by param()
    val altTitles: List<String> by param()
    private val textInputProperty = SimpleStringProperty()
    private val postController: PostController by inject()
    private lateinit var comboBox: ComboBox<String>

    override fun onDock() {
        textInputProperty.value = name
    }

    override val root = vbox(alignment = Pos.CENTER_RIGHT) {
        padding = insets(all = 5)
        spacing = 5.0
        form {
            fieldset {
                field("Name") {
                    comboBox = combobox(textInputProperty, altTitles.ifEmpty { listOf(name) }) {
                        useMaxSize = true
                        isEditable = true
                    }
                }
            }
        }
        button("Rename") {
            disableWhen(comboBox.editor.textProperty().isEmpty)
            action {
                postController.rename(postId, comboBox.editor.text.trim())
                close()
            }
        }
    }
}