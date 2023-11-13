package me.vripper.web.restendpoints

import me.vripper.model.PostItem
import me.vripper.model.ThreadPostId
import me.vripper.services.AppEndpointService
import me.vripper.web.restendpoints.domain.*
import me.vripper.web.restendpoints.exceptions.BadRequestException
import me.vripper.web.restendpoints.exceptions.ServerErrorException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class PostRestEndpoint : KoinComponent {

    private val log by me.vripper.delegate.LoggerDelegate()
    private val appEndpointService: AppEndpointService by inject()

    @PostMapping("/post")
    @ResponseStatus(code = HttpStatus.OK)
    fun scan(@RequestBody scanRequest: ScanRequest) {
        if (scanRequest.links.isNullOrBlank()) {
            log.error("Cannot process empty requests")
            throw BadRequestException("Cannot process empty requests")
        }
        appEndpointService.scanLinks(scanRequest.links)
    }

    @PostMapping("/post/restart")
    @ResponseStatus(value = HttpStatus.OK)
    fun start(@RequestBody postIds: List<Long>) {
        appEndpointService.restartAll(postIds)
    }

    @PostMapping("/post/add")
    @ResponseStatus(value = HttpStatus.OK)
    fun download(@RequestBody posts: List<ThreadPostId>) {
        appEndpointService.download(posts)
    }

    @PostMapping("/post/restart/all")
    @ResponseStatus(value = HttpStatus.OK)
    fun startAll() {
        appEndpointService.restartAll()
    }

    @PostMapping("/post/stop")
    @ResponseStatus(value = HttpStatus.OK)
    fun stop(@RequestBody postIds: List<Long>) {
        appEndpointService.stopAll(postIds)
    }

    @PostMapping("/post/stop/all")
    @ResponseStatus(value = HttpStatus.OK)
    fun stopAll() {
        appEndpointService.stopAll()
    }

    @PostMapping("/post/remove")
    @ResponseStatus(value = HttpStatus.OK)
    fun remove(@RequestBody postIds: List<Long>): List<Long> {
        appEndpointService.remove(postIds)
        return postIds
    }

    @PostMapping("/post/clear/all")
    @ResponseStatus(value = HttpStatus.OK)
    fun clearAll(): List<Long> {
        return appEndpointService.clearCompleted()
    }

    @GetMapping("/grab/{threadId}")
    @ResponseStatus(value = HttpStatus.OK)
    fun grab(@PathVariable("threadId") threadId: Long): List<PostItem> {
        return try {
            appEndpointService.grab(threadId)
        } catch (e: Exception) {
            throw ServerErrorException(
                String.format(
                    "Failed to get links for threadId = %s, %s",
                    threadId,
                    e.message
                )
            )
        }
    }

    @PostMapping("/grab/remove")
    @ResponseStatus(value = HttpStatus.OK)
    fun grabRemove(@RequestBody threadIds: List<Long>) {
        appEndpointService.threadRemove(threadIds)
    }

    @GetMapping("/grab/clear")
    @ResponseStatus(value = HttpStatus.OK)
    fun threadClear() {
        appEndpointService.threadClear()
    }
}