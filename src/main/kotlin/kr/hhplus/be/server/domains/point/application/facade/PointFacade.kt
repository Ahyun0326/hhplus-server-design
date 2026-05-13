package kr.hhplus.be.server.domains.point.application.facade

import kr.hhplus.be.server.domains.common.auth.AuthenticatedMemberReader
import kr.hhplus.be.server.domains.point.application.dto.request.ChargePointRequest
import kr.hhplus.be.server.domains.point.application.dto.response.PointResponse
import kr.hhplus.be.server.domains.point.application.usecase.ChargePointService
import kr.hhplus.be.server.domains.point.application.usecase.FindPointService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PointFacade(
    private val authenticatedMemberReader: AuthenticatedMemberReader,
    private val chargePointService: ChargePointService,
    private val findPointService: FindPointService
) {

    @Transactional
    fun chargePoint(uuid: String, request: ChargePointRequest) {
        val memberId = authenticatedMemberReader.resolveMemberId(uuid)
        chargePointService.invoke(memberId, request)
    }

    @Transactional(readOnly = true)
    fun findPoint(uuid: String): PointResponse {
        val memberId = authenticatedMemberReader.resolveMemberId(uuid)
        return findPointService.invoke(memberId)
    }
}
