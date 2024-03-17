package me.vripper.tasks

import kotlinx.coroutines.runBlocking
import me.vripper.entities.LogEntryEntity
import me.vripper.entities.LogEntryEntity.Status.ERROR
import me.vripper.entities.ThreadEntity
import me.vripper.model.Settings
import me.vripper.model.ThreadPostId
import me.vripper.parser.ThreadItem
import me.vripper.services.AppEndpointService
import me.vripper.services.DataTransaction
import me.vripper.services.SettingsService
import me.vripper.services.ThreadCacheService
import me.vripper.utilities.GLOBAL_EXECUTOR
import me.vripper.utilities.Tasks
import me.vripper.utilities.formatToString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CompletableFuture

class ThreadLookupRunnable(private val threadId: Long, private val settings: Settings) :
    KoinComponent, Runnable {
    private val log by me.vripper.delegate.LoggerDelegate()
    private val dataTransaction by inject<DataTransaction>()
    private val settingsService by inject<SettingsService>()
    private val threadCacheService by inject<ThreadCacheService>()
    private val appEndpointService by inject<AppEndpointService>()
    private val link: String = "${settingsService.settings.viperSettings.host}/threads/$threadId"

    override fun run() {
        try {
            Tasks.increment()
            if (dataTransaction.findThreadByThreadId(threadId).isEmpty) {
                val threadLookupResult = threadCacheService[threadId]
                if (threadLookupResult.postItemList.isEmpty()) {
                    val message = "Nothing found for $link"
                    dataTransaction.saveLog(
                        LogEntryEntity(
                            type = LogEntryEntity.Type.THREAD,
                            status = ERROR,
                            message = message
                        )
                    )
                    return
                }
                dataTransaction.save(
                    ThreadEntity(
                        title = threadLookupResult.title,
                        link = link,
                        threadId = threadId,
                        total = threadLookupResult.postItemList.size
                    )
                )
                autostart(threadLookupResult)
            } else {
                log.info("Link $link is already loaded")
            }
        } catch (e: Exception) {
            val error = "Error when processing $link"
            log.error(error, e)
            dataTransaction.saveLog(
                LogEntryEntity(
                    type = LogEntryEntity.Type.THREAD, status = ERROR, message = """
                $error
                ${e.formatToString()}
                """.trimIndent()
                )
            )
        } finally {
            Tasks.decrement()
        }
    }

    private fun autostart(lookupResult: ThreadItem) {
        if (lookupResult.postItemList.size <= settings.downloadSettings.autoQueueThreshold) {
            runBlocking {
                appEndpointService.threadRemove(listOf(lookupResult.threadId))
            }
            CompletableFuture.runAsync(
                AddPostRunnable(
                    lookupResult.postItemList.map { ThreadPostId(it.threadId, it.postId) }
                ),
                GLOBAL_EXECUTOR
            )
        }
    }
}