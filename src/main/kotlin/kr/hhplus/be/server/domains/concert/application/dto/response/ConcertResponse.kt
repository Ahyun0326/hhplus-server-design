package kr.hhplus.be.server.domains.concert.application.dto.response

import kr.hhplus.be.server.domains.concert.domain.repository.projection.ConcertQueryDto
import java.time.LocalDateTime

data class ConcertResponse(
    val concertId: Long,
    val title: String,
    val location: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val expired: Boolean
) {
    companion object {
        fun from(concertQueryDto: ConcertQueryDto): ConcertResponse {
            return ConcertResponse(
                concertQueryDto.concertId,
                concertQueryDto.title,
                concertQueryDto.location,
                concertQueryDto.startedAt,
                concertQueryDto.expiredAt,
                concertQueryDto.expiredAt.isBefore(LocalDateTime.now())
            )
        }
    }
}
