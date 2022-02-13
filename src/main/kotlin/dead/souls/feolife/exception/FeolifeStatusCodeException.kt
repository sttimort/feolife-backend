package dead.souls.feolife.exception

import org.springframework.http.HttpStatus

sealed class FeolifeStatusCodeException(
    val status: HttpStatus,
    val responseErrorMessage: String,
    override val message: String?,
) : RuntimeException(message)
