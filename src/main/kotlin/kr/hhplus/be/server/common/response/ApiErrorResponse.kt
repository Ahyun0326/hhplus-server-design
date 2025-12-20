package kr.hhplus.be.server.common.response

data class ApiErrorResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
) {
    companion object {
        fun of(code: Int, message: String): ApiErrorResponse {
            return ApiErrorResponse(
                success = false,
                code = code,
                message = message
            )
        }
    }
}