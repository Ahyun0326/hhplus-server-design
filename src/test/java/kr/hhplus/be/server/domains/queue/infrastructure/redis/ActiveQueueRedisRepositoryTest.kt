package kr.hhplus.be.server.domains.queue.infrastructure.redis

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domains.queue.infrastructure.config.QueueProperties
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.ZSetOperations
import java.util.concurrent.TimeUnit

class ActiveQueueRedisRepositoryTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val redisTemplate: RedisTemplate<String, Any> = mockk()
    val zSetOperations: ZSetOperations<String, Any> = mockk()
    val setOperations: SetOperations<String, Any> = mockk()
    val valueOperations: ValueOperations<String, Any> = mockk()
    val activeQueueRedisRepository = ActiveQueueRedisRepository(redisTemplate, QueueProperties())

    given("활성 토큰을 저장할 때") {
        val queueProperties = QueueProperties().apply {
            activeTokenTtlMs = 300_000
        }
        val repository = ActiveQueueRedisRepository(redisTemplate, queueProperties)

        every { redisTemplate.opsForSet() } returns setOperations
        every { redisTemplate.opsForZSet() } returns zSetOperations
        every { redisTemplate.opsForValue() } returns valueOperations
        every { setOperations.add("queue:schedule:active", "1") } returns 1
        every { zSetOperations.add("queue:schedule:1:active", any(), any()) } returns true
        every {
            valueOperations.set(
                "queue:schedule:1:active:uuid:member-uuid",
                any(),
                300_000,
                TimeUnit.MILLISECONDS
            )
        } returns Unit

        `when`("설정된 active token TTL로 토큰을 발급하면") {
            repository.saveActive(1L, "member-uuid")

            then("uuid 매핑 key에 active token TTL을 적용한다") {
                verify(exactly = 1) {
                    valueOperations.set(
                        "queue:schedule:1:active:uuid:member-uuid",
                        any(),
                        300_000,
                        TimeUnit.MILLISECONDS
                    )
                }
            }
        }
    }

    given("사용자에게 활성 토큰이 있을 때") {
        every { redisTemplate.opsForValue() } returns valueOperations
        every { redisTemplate.opsForZSet() } returns zSetOperations
        every { redisTemplate.opsForSet() } returns setOperations
        every { valueOperations.get("queue:schedule:1:active:uuid:member-uuid") } returns "queue-token"
        every { zSetOperations.remove("queue:schedule:1:active", "queue-token") } returns 1
        every { redisTemplate.delete("queue:schedule:1:active:uuid:member-uuid") } returns true
        every { zSetOperations.size("queue:schedule:1:active") } returns 0
        every { setOperations.remove("queue:schedule:active", "1") } returns 1

        `when`("활성 토큰을 제거하면") {
            activeQueueRedisRepository.removeActive(1L, "member-uuid")

            then("active ZSET과 uuid 매핑 key를 제거한다") {
                verify(exactly = 1) { zSetOperations.remove("queue:schedule:1:active", "queue-token") }
                verify(exactly = 1) { redisTemplate.delete("queue:schedule:1:active:uuid:member-uuid") }
                verify(exactly = 1) { setOperations.remove("queue:schedule:active", "1") }
            }
        }
    }

    given("사용자에게 활성 토큰이 없을 때") {
        every { redisTemplate.opsForValue() } returns valueOperations
        every { valueOperations.get("queue:schedule:1:active:uuid:member-uuid") } returns null

        `when`("활성 토큰을 제거하면") {
            activeQueueRedisRepository.removeActive(1L, "member-uuid")

            then("아무 Redis key도 제거하지 않는다") {
                verify(exactly = 0) { redisTemplate.delete(any<String>()) }
                verify(exactly = 0) { zSetOperations.remove(any(), any()) }
                verify(exactly = 0) { setOperations.remove(any(), any()) }
            }
        }
    }

    given("만료 토큰 정리 후 스케줄 활성 큐가 비었을 때") {
        every { redisTemplate.opsForZSet() } returns zSetOperations
        every { redisTemplate.opsForSet() } returns setOperations
        every { zSetOperations.removeRangeByScore("queue:schedule:1:active", 0.0, any()) } returns 1
        every { zSetOperations.size("queue:schedule:1:active") } returns 0
        every { setOperations.remove("queue:schedule:active", "1") } returns 1

        `when`("만료 활성 토큰을 제거하면") {
            activeQueueRedisRepository.removeExpiredActive(1L)

            then("스케줄 목록 set에서도 scheduleId를 제거한다") {
                verify(exactly = 1) { setOperations.remove("queue:schedule:active", "1") }
            }
        }
    }

    given("만료 토큰 정리 후 스케줄 활성 큐가 남아 있을 때") {
        every { redisTemplate.opsForZSet() } returns zSetOperations
        every { redisTemplate.opsForSet() } returns setOperations
        every { zSetOperations.removeRangeByScore("queue:schedule:1:active", 0.0, any()) } returns 1
        every { zSetOperations.size("queue:schedule:1:active") } returns 1

        `when`("만료 활성 토큰을 제거하면") {
            activeQueueRedisRepository.removeExpiredActive(1L)

            then("스케줄 목록 set에서 scheduleId를 제거하지 않는다") {
                verify(exactly = 0) { setOperations.remove(any(), any()) }
            }
        }
    }
})
