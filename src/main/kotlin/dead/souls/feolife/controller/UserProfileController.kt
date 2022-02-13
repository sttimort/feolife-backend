package dead.souls.feolife.controller

import dead.souls.feolife.exception.RequestBodyValidationException
import dead.souls.feolife.model.request.CreateUserProfileWithUsernamePasswordCredentialsRequest
import dead.souls.feolife.model.response.GetUserProfileCommonDataSetResponse
import dead.souls.feolife.service.UserProfileManager
import io.konform.validation.Constraint
import io.konform.validation.Invalid
import io.konform.validation.Validation
import io.konform.validation.ValidationBuilder
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class UserProfileController(
    private val userProfileManager: UserProfileManager,
) {
    @GetMapping("user-profile")
    fun getUserProfileCommonDataSet(
        @AuthenticationPrincipal userProfileUuid: UUID
    ): GetUserProfileCommonDataSetResponse =
        userProfileManager.getUserProfileCommonDataSet(userProfileUuid = userProfileUuid)

    @PostMapping("username-password-profiles")
    @ResponseStatus(HttpStatus.CREATED)
    fun createProfileWithUsernamePasswordCredentials(
        @RequestBody body: CreateUserProfileWithUsernamePasswordCredentialsRequest,
    ) {
        val validateBody = Validation<CreateUserProfileWithUsernamePasswordCredentialsRequest> {
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

        validateBody(body).also { if (it is Invalid) throw RequestBodyValidationException(it) }
        userProfileManager.createUserProfile(body)
    }
}

fun ValidationBuilder<String>.notBlank(): Constraint<String> =
    addConstraint("must not be blank") { it.isNotBlank() }

