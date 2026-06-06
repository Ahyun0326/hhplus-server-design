package kr.hhplus.be.server.domains.queue.presentation.web

import kr.hhplus.be.server.common.response.ApiResponse
import kr.hhplus.be.server.domains.queue.application.dto.request.IssueQueueTokenRequest
import kr.hhplus.be.server.domains.queue.application.dto.response.QueueTokenResponse
import kr.hhplus.be.server.domains.queue.application.facade.QueueFacade
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/queue/tokens")
class QueueController(
    private val queueFacade: QueueFacade
) {

    @PostMapping
    fun issueToken(
        @AuthenticationPrincipal uuid: String,
        @RequestBody request: IssueQueueTokenRequest
    ): ApiResponse<QueueTokenResponse> {
        return ApiResponse.success(queueFacade.issueToken(uuid, request.scheduleId))
    }

    @GetMapping("/me")
    fun getMyStatus(
        @AuthenticationPrincipal uuid: String,
        @RequestParam scheduleId: Long
    ): ApiResponse<QueueTokenResponse> {
        return ApiResponse.success(queueFacade.getMyStatus(uuid, scheduleId))
    }
}
