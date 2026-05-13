package kr.hhplus.be.server.domains.queue.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.common.exception.QueueTokenInvalidException
import kr.hhplus.be.server.domains.queue.domain.repository.ActiveQueueRepository

class ValidateQueueTokenServiceTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val activeQueueRepository: ActiveQueueRepository = mockk()
    val validateQueueTokenService = ValidateQueueTokenService(activeQueueRepository)

    given("인증된 uuid에 매핑된 활성 토큰으로") {
        val uuid = "member-uuid"
        val token = "queue-token"

        every { activeQueueRepository.findActive(uuid) } returns token
        every { activeQueueRepository.isValidToken(token) } returns true

        `when`("대기열 토큰을 검증하면") {
            then("검증을 통과한다") {
                validateQueueTokenService.validate(uuid, token)
            }
        }
    }

    given("다른 uuid에 매핑된 활성 토큰으로") {
        val uuid = "member-uuid"
        val token = "queue-token"

        every { activeQueueRepository.findActive(uuid) } returns "other-token"

        `when`("대기열 토큰을 검증하면") {
            then("QueueTokenInvalidException이 발생한다") {
                shouldThrow<QueueTokenInvalidException> {
                    validateQueueTokenService.validate(uuid, token)
                }
            }
        }
    }

    given("uuid에 매핑된 토큰이 만료된 경우") {
        val uuid = "member-uuid"
        val token = "queue-token"

        every { activeQueueRepository.findActive(uuid) } returns token
        every { activeQueueRepository.isValidToken(token) } returns false

        `when`("대기열 토큰을 검증하면") {
            then("QueueTokenInvalidException이 발생한다") {
                shouldThrow<QueueTokenInvalidException> {
                    validateQueueTokenService.validate(uuid, token)
                }
            }
        }
    }
})
