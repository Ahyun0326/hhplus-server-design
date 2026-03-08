package kr.hhplus.be.server.domains.payment.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface SpringPaymentJpa : JpaRepository<PaymentEntity, Long> {

}