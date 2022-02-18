package dead.souls.feolife.validation

import dead.souls.feolife.exception.RequestBodyValidationException
import io.konform.validation.Invalid
import io.konform.validation.Validation

fun <T : Any> Validation<T>.validateOrThrow(value: T) =
    this(value).also { if (it is Invalid) throw RequestBodyValidationException(it) }
