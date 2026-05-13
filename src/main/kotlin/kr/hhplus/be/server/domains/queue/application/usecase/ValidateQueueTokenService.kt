package kr.hhplus.be.server.domains.queue.application.usecase

import kr.hhplus.be.server.common.exception.QueueTokenInvalidException
import kr.hhplus.be.server.domains.queue.domain.repository.ActiveQueueRepository

class ValidateQueueTokenService(
    private val activeQueueRepository: ActiveQueueRepository
) {

    fun validate(uuid: String, token: String) {
        val activeToken = activeQueueRepository.findActive(uuid)

        if (activeToken != token || !activeQueueRepository.isValidToken(token)) {
            throw QueueTokenInvalidException()
        }
    }
}
