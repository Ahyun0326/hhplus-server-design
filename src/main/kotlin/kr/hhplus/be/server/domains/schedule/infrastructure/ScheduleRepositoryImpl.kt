package kr.hhplus.be.server.domains.schedule.infrastructure

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hhplus.be.server.domains.schedule.domain.model.QSchedule.Companion.schedule
import kr.hhplus.be.server.domains.schedule.domain.model.Schedule
import kr.hhplus.be.server.domains.schedule.domain.repository.ScheduleQueryRepositoryCustom
import org.springframework.stereotype.Repository

@Repository
class ScheduleRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ScheduleQueryRepositoryCustom {

    override fun findAvailableConcerts(concertId: Long): List<Schedule> {
        return queryFactory.selectFrom(schedule)
            .where(schedule.concert.id.eq(concertId))
            .fetch()
    }
}
