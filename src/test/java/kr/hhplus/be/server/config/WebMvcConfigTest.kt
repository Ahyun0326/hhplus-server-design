package kr.hhplus.be.server.config

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domains.queue.presentation.web.QueueTokenInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry

class WebMvcConfigTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val queueTokenInterceptor: QueueTokenInterceptor = mockk()
    val interceptorRegistry: InterceptorRegistry = mockk()
    val interceptorRegistration: InterceptorRegistration = mockk()
    val webMvcConfig = WebMvcConfig(queueTokenInterceptor)

    given("queue token interceptor를 등록할 때") {
        every { interceptorRegistry.addInterceptor(queueTokenInterceptor) } returns interceptorRegistration
        every { interceptorRegistration.addPathPatterns(any<List<String>>()) } returns interceptorRegistration

        `when`("WebMvcConfig가 interceptor를 추가하면") {
            webMvcConfig.addInterceptors(interceptorRegistry)

            then("예약 흐름은 보호하고 결제 API는 제외한다") {
                verify(exactly = 1) {
                    interceptorRegistration.addPathPatterns(
                        match<List<String>> {
                            "/api/v1/seats/**" in it &&
                                "/api/v1/reservation/seats/**" in it &&
                                "/api/v1/payments/**" !in it
                        }
                    )
                }
            }
        }
    }
})
