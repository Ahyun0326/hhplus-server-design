package kr.hhplus.be.server.domains.reservation.application.facade

import kr.hhplus.be.server.domains.common.auth.AuthenticatedMemberReader
import kr.hhplus.be.server.domains.common.queue.ActiveQueueTokenReleaser
import kr.hhplus.be.server.domains.reservation.application.dto.ReservationRequest
import kr.hhplus.be.server.domains.reservation.application.dto.ReservationResponse
import kr.hhplus.be.server.domains.reservation.application.usecase.ReserveSeatService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReservationFacade(
    private val authenticatedMemberReader: AuthenticatedMemberReader,
    private val reserveSeatService: ReserveSeatService,
    private val activeQueueTokenReleaser: ActiveQueueTokenReleaser
) {

    @Transactional
    fun reserveSeat(uuid: String, reservationRequest: ReservationRequest): ReservationResponse {
        val memberId = authenticatedMemberReader.resolveMemberId(uuid)
        val response = reserveSeatService.invoke(memberId, reservationRequest)

        activeQueueTokenReleaser.release(uuid, reservationRequest.scheduleId)

        return response
    }
}
