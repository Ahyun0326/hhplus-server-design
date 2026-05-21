package kr.hhplus.be.server.domains.reservation.application.facade

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domains.common.auth.AuthenticatedMemberReader
import kr.hhplus.be.server.domains.common.queue.ActiveQueueTokenReleaser
import kr.hhplus.be.server.domains.reservation.application.dto.ReservationRequest
import kr.hhplus.be.server.domains.reservation.application.dto.ReservationResponse
import kr.hhplus.be.server.domains.reservation.application.usecase.ReserveSeatService

class ReservationFacadeTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val authenticatedMemberReader: AuthenticatedMemberReader = mockk()
    val reserveSeatService: ReserveSeatService = mockk()
    val activeQueueTokenReleaser: ActiveQueueTokenReleaser = mockk()
    val reservationFacade = ReservationFacade(
        authenticatedMemberReader,
        reserveSeatService,
        activeQueueTokenReleaser
    )

    given("예약이 성공했을 때") {
        val uuid = "member-uuid"
        val memberId = 1L
        val request = ReservationRequest(scheduleId = 10L, seatIds = listOf(1L, 2L))
        val response = ReservationResponse(reservationId = 100L)

        every { authenticatedMemberReader.resolveMemberId(uuid) } returns memberId
        every { reserveSeatService.invoke(memberId, request) } returns response
        every { activeQueueTokenReleaser.release(uuid, request.scheduleId) } returns Unit

        `when`("예약 facade를 호출하면") {
            val result = reservationFacade.reserveSeat(uuid, request)

            then("예약 응답을 반환하고 활성 대기열 토큰을 제거한다") {
                result shouldBe response
                verify(exactly = 1) { activeQueueTokenReleaser.release(uuid, request.scheduleId) }
            }
        }
    }

    given("예약이 실패했을 때") {
        val uuid = "member-uuid"
        val memberId = 1L
        val request = ReservationRequest(scheduleId = 10L, seatIds = listOf(1L, 2L))
        val exception = RuntimeException("예약 실패")

        every { authenticatedMemberReader.resolveMemberId(uuid) } returns memberId
        every { reserveSeatService.invoke(memberId, request) } throws exception

        `when`("예약 facade를 호출하면") {
            then("활성 대기열 토큰을 제거하지 않는다") {
                shouldThrow<RuntimeException> {
                    reservationFacade.reserveSeat(uuid, request)
                }
                verify(exactly = 0) { activeQueueTokenReleaser.release(any(), any()) }
            }
        }
    }
})
