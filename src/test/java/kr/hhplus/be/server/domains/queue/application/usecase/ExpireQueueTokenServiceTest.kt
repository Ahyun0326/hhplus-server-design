package kr.hhplus.be.server.domains.queue.application.usecase

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kr.hhplus.be.server.domains.queue.domain.repository.ActiveQueueRepository

class ExpireQueueTokenServiceTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val activeQueueRepository: ActiveQueueRepository = mockk()
    val expireQueueTokenService = ExpireQueueTokenService(activeQueueRepository)

    given("활성 큐가 존재하는 스케줄들이 있을 때") {
        every { activeQueueRepository.findScheduleIds() } returns listOf(1L, 2L)
        every { activeQueueRepository.removeExpiredActive(any()) } just runs

        `when`("만료 토큰을 정리하면") {
            expireQueueTokenService.execute()

            then("스케줄별 활성 큐에서 만료 토큰을 제거한다") {
                verify(exactly = 1) { activeQueueRepository.removeExpiredActive(1L) }
                verify(exactly = 1) { activeQueueRepository.removeExpiredActive(2L) }
            }
        }
    }
})
