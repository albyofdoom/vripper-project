package me.vripper.gui.model

import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import me.vripper.entities.Metadata
import tornadofx.*

class PostModel(
    postId: Long,
    title: String,
    progress: Double,
    status: String,
    url: String,
    done: Int,
    total: Int,
    hosts: String,
    addedOn: String,
    order: Int,
    path: String,
    folderName: String,
    progressCount: String,
    previewList: List<String>,
    metadata: Metadata
) {
    val postIdProperty = SimpleLongProperty(postId)
    var postId: Long by postIdProperty

    val titleProperty = SimpleStringProperty(title)
    var title: String by titleProperty

    val progressProperty = SimpleDoubleProperty(progress)
    var progress: Double by progressProperty

    val statusProperty = SimpleStringProperty(status)
    var status: String by statusProperty

    val urlProperty = SimpleStringProperty(url)
    var url: String by urlProperty

    val doneProperty = SimpleIntegerProperty(done)
    var done: Int by doneProperty

    val totalProperty = SimpleIntegerProperty(total)
    var total: Int by totalProperty

    val hostsProperty = SimpleStringProperty(hosts)
    var hosts: String by hostsProperty

    val addedOnProperty = SimpleStringProperty(addedOn)
    var addedOn: String by addedOnProperty

    val orderProperty = SimpleIntegerProperty(order)
    var order: Int by orderProperty

    val pathProperty = SimpleStringProperty(path)
    var path: String by pathProperty

    val folderNameProperty = SimpleStringProperty(folderName)
    var folderName: String by folderNameProperty

    val progressCountProperty = SimpleStringProperty(progressCount)
    var progressCount: String by progressCountProperty

    val previewListProperty = SimpleStringProperty(previewList.joinToString("|"))
    var previewList: List<String> = previewListProperty.value.split("|")

    val altTitlesProperty =
        SimpleListProperty(FXCollections.observableArrayList(metadata.data.resolvedNames))
    var altTitles: ObservableList<String> by altTitlesProperty

    val postedByProperty = SimpleStringProperty(metadata.data.postedBy)
    var postedby: String by postedByProperty
}
