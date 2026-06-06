package kr.hhplus.be.server.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiResponse<T>(
    val success: Boolean = true,
    val code: Int = HttpStatus.OK.value(),
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T, message: String = "요청에 성공하였습니다."): ApiResponse<T> {
            return ApiResponse(
                message = message,
                data = data
            )
        }

        fun success(message: String = "요청에 성공하였습니다."): ApiResponse<Unit> {
            return ApiResponse(
                message = message
            )
        }
    }
}