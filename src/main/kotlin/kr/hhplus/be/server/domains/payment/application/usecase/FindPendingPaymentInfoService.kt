package kr.hhplus.be.server.domains.payment.application.usecase

import kr.hhplus.be.server.common.exception.PaymentInfoNotFoundException
import kr.hhplus.be.server.common.exception.ReservationNotFoundException
import kr.hhplus.be.server.common.exception.ReservationSeatNotFoundException
import kr.hhplus.be.server.domains.payment.application.dto.PendingPaymentInfoResponse
import kr.hhplus.be.server.domains.payment.domain.repository.PaymentRepository
import kr.hhplus.be.server.domains.reservation.domain.repository.ReservationRepository
import kr.hhplus.be.server.domains.seat.application.validator.SeatValidator
import kr.hhplus.be.server.domains.seat.domain.repository.SeatRepository

class FindPendingPaymentInfoService(
    private val reservationRepository: ReservationRepository,
    private val seatRepository: SeatRepository,
) {
    fun invoke(reservationId: Long): PendingPaymentInfoResponse {
        /**
        "username": "testUser", // 회원 도메인 - 보류
        "title": "yb 서울 콘서트", // 콘서트 도메인
        "viewingTime": 2,       // 스케줄 도메인
        "datetime": "2025-11-13T17:00:00", // 스케줄 도메인
        "seatNumbers": ["A1", "A2"], // 좌석 도메인
        "price": 100000 // 좌석 도메인
         */

        // 예약 정보 조회
        val reservation = reservationRepository.findById(reservationId)
            ?: throw ReservationNotFoundException()

        // 예약 정보에 해당하는 좌석이 없는 경우
        val seats = seatRepository.findSeatsByReservationId(reservation.id)
        check(seats.isNotEmpty()) { throw ReservationSeatNotFoundException() }

        // 회원이 좌석을 점유하고 있는지 확인
//        seatValidator.validateExpiredSeats(
//            seats.filter { !it.isAvailable() }.map { it.id }
//        )

        // 좌석 점유 시간이 만료되지 않았는지 확인
//        seatValidator.validateExpiredSeats(
//            seats.filter { it.isAvailable() }.map { it.id }
//        )

        val reservationPaymentDetailQueryDto =
            reservationRepository.getWithDetailsById(reservationId) ?: throw PaymentInfoNotFoundException()

        return PendingPaymentInfoResponse.from(reservationPaymentDetailQueryDto)
    }

}