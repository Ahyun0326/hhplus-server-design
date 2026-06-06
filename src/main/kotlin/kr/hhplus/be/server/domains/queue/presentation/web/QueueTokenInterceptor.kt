package kr.hhplus.be.server.domains.queue.presentation.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.hhplus.be.server.common.exception.QueueTokenInvalidException
import kr.hhplus.be.server.domains.queue.application.facade.QueueFacade
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.servlet.HandlerInterceptor

@Component
class QueueTokenInterceptor(
    private val queueFacade: QueueFacade
) : HandlerInterceptor {

    companion object {
        private const val QUEUE_TOKEN_HEADER = "X-Queue-Token"
        private const val QUEUE_SCHEDULE_ID_HEADER = "X-Queue-Schedule-Id"
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = request.getHeader(QUEUE_TOKEN_HEADER)
            ?: throw QueueTokenInvalidException()

        val scheduleId = resolveScheduleId(request)

        val uuid = SecurityContextHolder.getContext().authentication?.principal as? String
            ?: throw QueueTokenInvalidException()

        queueFacade.validateToken(uuid, scheduleId, token)
        return true
    }

    private fun resolveScheduleId(request: HttpServletRequest): Long {
        val pathVariables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE) as? Map<*, *>
        val pathScheduleId = pathVariables?.get("scheduleId")?.toString()?.toLongOrNull()

        return pathScheduleId
            ?: request.getHeader(QUEUE_SCHEDULE_ID_HEADER)?.toLongOrNull()
            ?: throw QueueTokenInvalidException()
    }

}
