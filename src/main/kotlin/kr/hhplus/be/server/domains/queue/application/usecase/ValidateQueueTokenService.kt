package kr.hhplus.be.server.domains.queue.application.usecase

import kr.hhplus.be.server.common.exception.QueueTokenInvalidException
import kr.hhplus.be.server.domains.queue.domain.repository.ActiveQueueRepository

class ValidateQueueTokenService(
    private val activeQueueRepository: ActiveQueueRepository
) {

    fun validate(uuid: String, scheduleId: Long, token: String) {
        val activeToken = activeQueueRepository.findActive(scheduleId, uuid)

        if (activeToken != token || !activeQueueRepository.isValidToken(scheduleId, token)) {
            throw QueueTokenInvalidException()
        }
    }
}
