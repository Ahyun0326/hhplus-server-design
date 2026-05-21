package kr.hhplus.be.server.domains.common.queue

interface ActiveQueueTokenReleaser {
    fun release(uuid: String, scheduleId: Long)
}
