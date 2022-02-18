package dead.souls.feolife.validation.request

import dead.souls.feolife.model.request.CreateUserProfileWithUsernamePasswordCredentialsRequest
import dead.souls.feolife.validation.notBlank
import dead.souls.feolife.validation.validateOrThrow
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength

private val createUserProfileWithUsernamePasswordCredentialsRequestValidation =
    Validation<CreateUserProfileWithUsernamePasswordCredentialsRequest> {
        CreateUserProfileWithUsernamePasswordCredentialsRequest::username {
            minLength(3)
            maxLength(128)
        }

        CreateUserProfileWithUsernamePasswordCredentialsRequest::password {
            minLength(6)
        }

        CreateUserProfileWithUsernamePasswordCredentialsRequest::firstName {
            notBlank()
            maxLength(128)
        }

        CreateUserProfileWithUsernamePasswordCredentialsRequest::lastName {
            notBlank()
            maxLength(128)
        }

        CreateUserProfileWithUsernamePasswordCredentialsRequest::middleName ifPresent {
            notBlank()
            maxLength(128)
        }
    }

fun CreateUserProfileWithUsernamePasswordCredentialsRequest.validate() =
    createUserProfileWithUsernamePasswordCredentialsRequestValidation.validateOrThrow(this)
