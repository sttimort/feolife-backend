package dead.souls.feolife.exception

import io.konform.validation.Invalid

class RequestBodyValidationException(
    val validationResult: Invalid<out Any>
) : RuntimeException("Validation failed $validationResult")
