package kr.hhplus.be.server.support.seeder

import kr.hhplus.be.server.domains.concert.domain.Concert
import kr.hhplus.be.server.domains.concert.infrastructure.ConcertRepository
import kr.hhplus.be.server.domains.schedule.domain.Schedule
import kr.hhplus.be.server.domains.schedule.infrastructure.ScheduleRepository
import kr.hhplus.be.server.domains.seat.domain.Seat
import kr.hhplus.be.server.domains.seat.infrastructure.SeatRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class TestDataSeeder(
    private val concertRepository: ConcertRepository,
    private val scheduleRepository: ScheduleRepository,
    private val seatRepository: SeatRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        if (concertRepository.count() > 0) return

        val seatsToSave = mutableListOf<Seat>()

        for (i in 1..10) {
            val savedConcert = concertRepository.save(
                Concert(
                    title = "콘서트${i}",
                    location = "장소${i}"
                )
            )

            for (j in 1..2) {
                val concertedAt = LocalDate.now().plusDays(i.toLong()).atTime(17, 30)

                val savedSchedule = scheduleRepository.save(
                    Schedule(
                        concert = savedConcert,
                        concertedAt = concertedAt,
                        viewingTime = 2
                    )
                )

                for (k in 1..50) {
                    val seat = Seat(
                        schedule = savedSchedule,
                        number = "S${k}",
                        price = 100000
                    )
                    seatsToSave.add(seat)
                }
            }
            seatRepository.saveAll(seatsToSave)
        }
    }

}