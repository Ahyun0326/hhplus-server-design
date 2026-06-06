package kr.hhplus.be.server.domains.queue.infrastructure

import kr.hhplus.be.server.domains.common.queue.ActiveQueueTokenReleaser
import kr.hhplus.be.server.domains.queue.domain.repository.ActiveQueueRepository
import org.springframework.stereotype.Component

@Component
class QueueActiveQueueTokenReleaser(
    private val activeQueueRepository: ActiveQueueRepository
) : ActiveQueueTokenReleaser {

    override fun release(uuid: String, scheduleId: Long) {
        activeQueueRepository.removeActive(scheduleId, uuid)
    }
}
