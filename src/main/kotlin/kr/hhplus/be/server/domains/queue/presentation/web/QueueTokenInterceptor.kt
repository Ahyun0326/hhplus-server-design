package kr.hhplus.be.server.domains.queue.presentation.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.hhplus.be.server.common.exception.QueueTokenInvalidException
import kr.hhplus.be.server.domains.queue.application.facade.QueueFacade
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class QueueTokenInterceptor(
    private val queueFacade: QueueFacade
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = request.getHeader("X-Queue-Token")
            ?: throw QueueTokenInvalidException()

        val uuid = SecurityContextHolder.getContext().authentication?.principal as? String
            ?: throw QueueTokenInvalidException()

        queueFacade.validateToken(uuid, token)
        return true
    }

}
