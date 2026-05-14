package kr.hhplus.be.server.domains.queue.domain.repository

interface ActiveQueueRepository {
    fun countActive(scheduleId: Long): Long
    fun saveActive(scheduleId: Long, uuid: String): String
    fun findActive(scheduleId: Long, uuid: String): String?
    fun getRemainingSeconds(scheduleId: Long, uuid: String): Long
    fun removeExpiredActive(scheduleId: Long)
    fun findScheduleIds(): List<Long>
    fun isValidToken(scheduleId: Long, token: String): Boolean
}
