package kr.hhplus.be.server.domains.reservation.application.facade

import kr.hhplus.be.server.domains.common.auth.AuthenticatedMemberReader
import kr.hhplus.be.server.domains.reservation.application.dto.ReservationRequest
import kr.hhplus.be.server.domains.reservation.application.dto.ReservationResponse
import kr.hhplus.be.server.domains.reservation.application.usecase.ReserveSeatService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReservationFacade(
    private val authenticatedMemberReader: AuthenticatedMemberReader,
    private val reserveSeatService: ReserveSeatService
) {

    @Transactional
    fun reserveSeat(uuid: String, reservationRequest: ReservationRequest): ReservationResponse {
        val memberId = authenticatedMemberReader.resolveMemberId(uuid)
        return reserveSeatService.invoke(memberId, reservationRequest)
    }
}
