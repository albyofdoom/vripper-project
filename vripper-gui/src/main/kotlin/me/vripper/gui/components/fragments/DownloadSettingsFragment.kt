package me.vripper.gui.components.fragments

import atlantafx.base.util.IntegerStringConverter
import javafx.scene.control.Spinner
import me.vripper.gui.controller.SettingsController
import me.vripper.gui.model.settings.DownloadSettingsModel
import tornadofx.*

class DownloadSettingsFragment : Fragment("Download Settings") {

    private val settingsController: SettingsController by inject()
    private val downloadSettings = settingsController.findDownloadSettings()
    val downloadSettingsModel = DownloadSettingsModel()

    override fun onDock() {
        downloadSettingsModel.downloadPath = downloadSettings.downloadPath
        downloadSettingsModel.autoStart = downloadSettings.autoStart
        downloadSettingsModel.forceOrder = downloadSettings.forceOrder
        downloadSettingsModel.forumSubfolder = downloadSettings.forumSubDirectory
        downloadSettingsModel.threadSubLocation = downloadSettings.threadSubLocation
        downloadSettingsModel.clearCompleted = downloadSettings.clearCompleted
        downloadSettingsModel.appendPostId = downloadSettings.appendPostId
    }

    override val root = vbox {
        form {
            fieldset {
                field("Download Path") {
                    textfield(downloadSettingsModel.downloadPathProperty) {
                        isEditable = false
                    }
                    button("Browse") {
                        action {
                            val directory = chooseDirectory(title = "Select download folder")
                            if (directory != null) {
                                downloadSettingsModel.downloadPathProperty.set(directory.path)
                            }
                        }
                    }
                }
                field("Auto start downloads") {
                    checkbox {
                        bind(downloadSettingsModel.autoStartProperty)
                    }
                }
                field("Auto queue thread if post count is below or equal to") {
                    add(Spinner<Int>(1, Int.MAX_VALUE, downloadSettings.autoQueueThreshold).apply {
                        downloadSettingsModel.autoQueueThresholdProperty.bind(valueProperty())
                        isEditable = true
                        IntegerStringConverter.createFor(this)
                    })
                }
                field("Organize by category") {
                    checkbox {
                        bind(downloadSettingsModel.forumSubfolderProperty)
                    }
                }
                field("Organize by thread") {
                    checkbox {
                        bind(downloadSettingsModel.threadSubLocationProperty)
                    }
                }
                field("Order images") {
                    checkbox {
                        bind(downloadSettingsModel.forceOrderProperty)
                    }
                }
                field("Append post id to download folder") {
                    checkbox {
                        bind(downloadSettingsModel.appendPostIdProperty)
                    }
                }
                field("Clear Finished") {
                    checkbox {
                        bind(downloadSettingsModel.clearCompletedProperty)
                    }
                }
            }
        }
    }
}