package kr.hhplus.be.server.domains.payment.domain.model

import io.hypersistence.tsid.TSID
import java.time.LocalDateTime

class Payment(
    val reservationId: Long,
    val number: String,
    var price: Int,
    var status: String,
    val paymentLog: PaymentLog,
) {
    val id: Long = 0L
    val paidAt: LocalDateTime? = null

    companion object {
        fun create(reservationId: Long, price: Int, paymentLog: PaymentLog): Payment {
            return Payment(
                reservationId = reservationId,
                number = TSID.fast().toLowerCase(),
                price = price,
                status = PaymentStatus.PAID.name,
                paymentLog = paymentLog
            )
        }
    }

}