package kr.hhplus.be.server.domains.payment.application.facade

import kr.hhplus.be.server.domains.common.auth.AuthenticatedMemberReader
import kr.hhplus.be.server.domains.common.queue.ActiveQueueTokenReleaser
import kr.hhplus.be.server.domains.payment.application.dto.PaymentRequest
import kr.hhplus.be.server.domains.payment.application.dto.PaymentResponse
import kr.hhplus.be.server.domains.payment.application.dto.PendingPaymentInfoResponse
import kr.hhplus.be.server.domains.payment.application.usecase.FindPendingPaymentInfoService
import kr.hhplus.be.server.domains.payment.application.usecase.ProcessPaymentService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentFacade(
    private val authenticatedMemberReader: AuthenticatedMemberReader,
    private val findPendingPaymentInfoService: FindPendingPaymentInfoService,
    private val processPaymentService: ProcessPaymentService,
    private val activeQueueTokenReleaser: ActiveQueueTokenReleaser
) {

    @Transactional(readOnly = true)
    fun findPendingPaymentInfo(uuid: String, reservationId: Long): PendingPaymentInfoResponse {
        val memberId = authenticatedMemberReader.resolveMemberId(uuid)
        return findPendingPaymentInfoService.invoke(memberId, reservationId)
    }

    @Transactional
    fun processPayment(uuid: String, request: PaymentRequest): PaymentResponse {
        val memberId = authenticatedMemberReader.resolveMemberId(uuid)
        val response = processPaymentService.process(memberId, request)
        activeQueueTokenReleaser.release(uuid, request.scheduleId)

        return response
    }
}
