package dead.souls.feolife.exception

import org.springframework.http.HttpStatus

open class FeolifeStatusConflictException(
    responseErrorMessage: String,
    message: String = responseErrorMessage,
) : FeolifeStatusCodeException(
    status = HttpStatus.CONFLICT,
    responseErrorMessage = responseErrorMessage,
    message = message,
)
