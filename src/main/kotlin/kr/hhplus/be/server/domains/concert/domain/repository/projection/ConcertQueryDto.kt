package kr.hhplus.be.server.domains.concert.domain.repository.projection

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

@QueryProjection
data class ConcertQueryDto(
    val concertId: Long,
    val title: String,
    val location: String,
    val startedAt: LocalDateTime,
    val expiredAt: LocalDateTime
) {}
