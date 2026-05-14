package kr.hhplus.be.server.domains.queue.infrastructure.redis

import kr.hhplus.be.server.domains.queue.domain.repository.WaitingQueueRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class WaitingQueueRedisRepository(
    private val redisTemplate: RedisTemplate<String, Any>
) : WaitingQueueRepository {

    companion object {
        private const val ZSET_KEY_PATTERN = "queue:schedule:%d:waiting"
        private const val SCHEDULE_SET_KEY = "queue:schedule:waiting"
        private const val TTL_MS = 10 * 60 * 1000L
    }

    override fun add(scheduleId: Long, uuid: String) {
        val expirationTime = System.currentTimeMillis() + TTL_MS
        redisTemplate.opsForSet().add(SCHEDULE_SET_KEY, scheduleId.toString())
        redisTemplate.opsForZSet().add(waitingKey(scheduleId), uuid, expirationTime.toDouble())
    }

    override fun getRank(scheduleId: Long, uuid: String): Long? =
        redisTemplate.opsForZSet().rank(waitingKey(scheduleId), uuid)?.let { it + 1 }

    // 빈 슬롯만큼 대기자를 활성 상태로 승격
    override fun popWaiting(scheduleId: Long, count: Long): List<String> =
        redisTemplate.opsForZSet().popMin(waitingKey(scheduleId), count)
            ?.mapNotNull { it.value as? String }
            ?: emptyList()

    override fun isWaiting(scheduleId: Long, uuid: String): Boolean =
        redisTemplate.opsForZSet().score(waitingKey(scheduleId), uuid) != null

    override fun findScheduleIds(): List<Long> {
        return redisTemplate.opsForSet().members(SCHEDULE_SET_KEY)
            ?.mapNotNull { it.toString().toLongOrNull() }
            ?: emptyList()
    }

    private fun waitingKey(scheduleId: Long): String =
        String.format(ZSET_KEY_PATTERN, scheduleId)
}
