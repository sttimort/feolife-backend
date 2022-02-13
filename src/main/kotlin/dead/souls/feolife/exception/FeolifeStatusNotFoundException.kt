package dead.souls.feolife.exception

import org.springframework.http.HttpStatus

class FeolifeStatusNotFoundException(
    responseErrorMessage: String,
    message: String = responseErrorMessage,
) : FeolifeStatusCodeException(
    status = HttpStatus.NOT_FOUND,
    responseErrorMessage = responseErrorMessage,
    message = message,
)
