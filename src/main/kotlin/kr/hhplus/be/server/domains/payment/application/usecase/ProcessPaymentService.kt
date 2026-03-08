package kr.hhplus.be.server.domains.payment.application.usecase

import kr.hhplus.be.server.common.exception.PaymentInfoNotFoundException
import kr.hhplus.be.server.common.exception.ReservationNotFoundException
import kr.hhplus.be.server.common.exception.ReservationSeatExpiredException
import kr.hhplus.be.server.domains.payment.application.dto.PaymentRequest
import kr.hhplus.be.server.domains.payment.domain.model.Payment
import kr.hhplus.be.server.domains.payment.domain.model.PaymentLog
import kr.hhplus.be.server.domains.payment.domain.repository.PaymentRepository
import kr.hhplus.be.server.domains.point.application.validator.PointValidator
import kr.hhplus.be.server.domains.point.domain.model.PointHistory
import kr.hhplus.be.server.domains.point.domain.model.PointHistoryType
import kr.hhplus.be.server.domains.point.domain.repository.PointHistoryRepository
import kr.hhplus.be.server.domains.point.domain.repository.PointRepository
import kr.hhplus.be.server.domains.reservation.domain.repository.ReservationRepository
import kr.hhplus.be.server.domains.seat.domain.repository.SeatHoldRepository
import kr.hhplus.be.server.domains.seat.domain.repository.SeatRepository

class ProcessPaymentService(
    private val reservationRepository: ReservationRepository,
    private val seatRepository: SeatRepository,
    private val seatHoldRepository: SeatHoldRepository,
    private val pointRepository: PointRepository,
    private val pointHistoryRepository: PointHistoryRepository,
    private val paymentRepository: PaymentRepository,
    private val pointValidator: PointValidator
) {
    fun process(request: PaymentRequest) {
        // 예약 id로 예약 정보 조회
        val reservation = reservationRepository.findById(request.reservationId)
            ?: throw ReservationNotFoundException()

        // 배정된 좌석의 예약 유효 시간이 지났을 경우 결제 실패
        val seats = seatRepository.findSeatsByReservationId(reservation.id)
        check(seats.isNotEmpty()) { throw ReservationSeatExpiredException() }

        val memberId = 1L
        val findPoint = pointRepository.findPointByMemberId(memberId)
            .orElseThrow()

        // 요청으로 들어온 포인트가 음수일 경우 결제 실패
        pointValidator.validateNegativePoint(request.point)

        // 실제 결제 금액 조회
        val paymentAmount = seats.sumOf { it.price }
        // 요청으로 들어온 결제 금액이 현재 결제 금액과 일치하지 않을 경우 결제 실패
        pointValidator.validatePaymentAmountMatch(paymentAmount, request.point)

        // 회원의 포인트가 결제 금액보다 부족할 경우 결제 실패
        pointValidator.validateInsufficientPoint(findPoint.point, request.point)

        // 유효성 검증 성공 시 좌석 상태 변경 및 redis에서 제거
        seatRepository.updateStatusToReserved(seats.map { it.id })
        seatHoldRepository.removeSeats(seats.map { it.id })

        // 회원 포인트 감소
        val remainPoint = findPoint.usePoint(request.point)
        pointRepository.save(findPoint)
        pointHistoryRepository.save(PointHistory.create(memberId, remainPoint, request.point, PointHistoryType.USE))

        // 결제 정보 생성
        val reservationPaymentDetailQueryDto = reservationRepository.getWithDetailsById(reservation.id)
            ?: throw PaymentInfoNotFoundException()

        val paymentLog = PaymentLog.create(reservationPaymentDetailQueryDto)

        paymentRepository.save(
            Payment.create(reservation.id, request.point, paymentLog), reservation
        )

    }
}