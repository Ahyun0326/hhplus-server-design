package kr.hhplus.be.server.domains.payment.application.facade

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domains.common.auth.AuthenticatedMemberReader
import kr.hhplus.be.server.domains.common.queue.ActiveQueueTokenReleaser
import kr.hhplus.be.server.domains.payment.application.dto.PaymentRequest
import kr.hhplus.be.server.domains.payment.application.dto.PaymentResponse
import kr.hhplus.be.server.domains.payment.application.usecase.FindPendingPaymentInfoService
import kr.hhplus.be.server.domains.payment.application.usecase.ProcessPaymentService
import java.time.LocalDateTime

class PaymentFacadeTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val authenticatedMemberReader: AuthenticatedMemberReader = mockk()
    val findPendingPaymentInfoService: FindPendingPaymentInfoService = mockk()
    val processPaymentService: ProcessPaymentService = mockk()
    val activeQueueTokenReleaser: ActiveQueueTokenReleaser = mockk()
    val paymentFacade = PaymentFacade(
        authenticatedMemberReader,
        findPendingPaymentInfoService,
        processPaymentService,
        activeQueueTokenReleaser
    )

    given("결제가 성공했을 때") {
        val uuid = "member-uuid"
        val memberId = 1L
        val request = PaymentRequest(
            reservationId = 10L,
            point = 1000,
            scheduleId = 20L
        )
        val response = PaymentResponse(
            paymentId = 100L,
            paymentNumber = "payment-number",
            amount = 1000,
            status = "PAID",
            paidAt = LocalDateTime.now()
        )

        every { authenticatedMemberReader.resolveMemberId(uuid) } returns memberId
        every { processPaymentService.process(memberId, request) } returns response
        every { activeQueueTokenReleaser.release(uuid, request.scheduleId) } returns Unit

        `when`("결제 facade를 호출하면") {
            val result = paymentFacade.processPayment(uuid, request)

            then("결제 응답을 반환하고 활성 대기열 토큰을 제거한다") {
                result shouldBe response
                verify(exactly = 1) { activeQueueTokenReleaser.release(uuid, request.scheduleId) }
            }
        }
    }

    given("결제가 실패했을 때") {
        val uuid = "member-uuid"
        val memberId = 1L
        val request = PaymentRequest(
            reservationId = 10L,
            point = 1000,
            scheduleId = 20L
        )
        val exception = RuntimeException("결제 실패")

        every { authenticatedMemberReader.resolveMemberId(uuid) } returns memberId
        every { processPaymentService.process(memberId, request) } throws exception

        `when`("결제 facade를 호출하면") {
            then("활성 대기열 토큰을 제거하지 않고 예외가 전파된다") {
                shouldThrow<RuntimeException> {
                    paymentFacade.processPayment(uuid, request)
                }
                verify(exactly = 0) { activeQueueTokenReleaser.release(any(), any()) }
            }
        }
    }
})
