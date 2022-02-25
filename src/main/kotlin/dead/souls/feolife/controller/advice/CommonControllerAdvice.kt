package dead.souls.feolife.controller.advice

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import dead.souls.feolife.exception.FeolifeStatusCodeException
import dead.souls.feolife.exception.RequestBodyValidationException
import dead.souls.feolife.logger
import dead.souls.feolife.model.response.FeolifeErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class CommonControllerAdvice {
    @ExceptionHandler
    fun methodArgumentNotValidExceptionHandler(
        exception: MethodArgumentNotValidException,
    ): ResponseEntity<FeolifeErrorResponse> {
        val errorMessage = exception.allErrors.firstOrNull()
            ?.let { if (it is FieldError) "${it.field} ${it.defaultMessage}" else it.defaultMessage }
            .orEmpty()

        val response = ResponseEntity.badRequest().body(FeolifeErrorResponse(errorMessage))

        log.info(exception) { "Responding with $response" }
        return response
    }

    @ExceptionHandler
    fun httpMessageNotReadableExceptionHandler(
        exception: HttpMessageNotReadableException,
    ): ResponseEntity<FeolifeErrorResponse> {
        val errorMessage = (exception.cause as? MismatchedInputException)
            ?.let { cause ->
                val fieldPath = cause.path
                    .joinToString { pathItem ->
                        when {
                            pathItem.fieldName != null -> ".${pathItem.fieldName}"
                            pathItem.index > 0 -> "[${pathItem.index}]"
                            else -> ""
                        }
                    }
                    .trimStart('.')

                "Invalid value for $fieldPath"
            }
            .orEmpty()

        return ResponseEntity.badRequest().body(FeolifeErrorResponse(errorMessage)).also {
            log.info(exception) { "Responding with $it" }
        }
    }

    @ExceptionHandler
    fun requestBodyValidationExceptionHandler(
        exception: RequestBodyValidationException,
    ): ResponseEntity<FeolifeErrorResponse> {
        val errorMessage = exception.validationResult.errors.firstOrNull()
            ?.let { "${it.dataPath} ${it.message}" }
            .orEmpty()

        return ResponseEntity.badRequest().body(FeolifeErrorResponse(errorMessage)).also {
            log.info(exception) { "Responding with $it" }
        }
    }

    @ExceptionHandler
    fun statusExceptionHandler(exception: FeolifeStatusCodeException): ResponseEntity<FeolifeErrorResponse> =
        ResponseEntity.status(exception.status.value())
            .body(FeolifeErrorResponse(errorMessage = exception.responseErrorMessage))
            .also {
                log.error(exception) { "Responding with $it" }
            }

    @ExceptionHandler
    fun methodArgumentTypeMismatchExceptionHandler(
        exception: MethodArgumentTypeMismatchException
    ): ResponseEntity<FeolifeErrorResponse> {
        val response = ResponseEntity.badRequest().body(FeolifeErrorResponse("Invalid request parameters"))

        log.info(exception) { "Responding with $response" }
        return response
    }

    @ExceptionHandler
    fun exceptionHandler(exception: Exception): ResponseEntity<FeolifeErrorResponse> =
        ResponseEntity.internalServerError().body(FeolifeErrorResponse(errorMessage = "Internal server error")).also {
            log.error(exception) { "Responding with $it" }
        }

    companion object {
        private val log by logger()
    }
}
