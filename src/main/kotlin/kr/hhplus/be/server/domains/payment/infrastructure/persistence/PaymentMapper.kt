package kr.hhplus.be.server.domains.payment.infrastructure.persistence

import kr.hhplus.be.server.domains.payment.domain.model.Payment
import kr.hhplus.be.server.domains.payment.infrastructure.persistence.dto.PaymentLogDto
import kr.hhplus.be.server.domains.reservation.infrastructure.persistence.ReservationEntity
import org.springframework.stereotype.Component

@Component
class PaymentMapper {
    fun toEntity(payment: Payment, reservationEntity: ReservationEntity): PaymentEntity {
        return PaymentEntity(
            reservationEntity,
            payment.number,
            payment.price,
            payment.status,
            PaymentLogDto.from(payment.paymentLog)
        )

    }

}