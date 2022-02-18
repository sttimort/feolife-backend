package dead.souls.feolife.controller

import dead.souls.feolife.model.FeolifePrincipal
import dead.souls.feolife.model.request.CreateUserProfileWithUsernamePasswordCredentialsRequest
import dead.souls.feolife.model.response.GetUserProfileCommonDataSetResponse
import dead.souls.feolife.service.UserProfileManager
import dead.souls.feolife.validation.notBlank
import dead.souls.feolife.validation.request.validate
import dead.souls.feolife.validation.validateOrThrow
import io.konform.validation.Validation
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
        @AuthenticationPrincipal principal: FeolifePrincipal,
    ): GetUserProfileCommonDataSetResponse =
        userProfileManager.getUserProfileCommonDataSet(userProfileUuid = principal.userProfileUuid)

    @PostMapping("username-password-profiles")
    @ResponseStatus(HttpStatus.CREATED)
    fun createProfileWithUsernamePasswordCredentials(
        @RequestBody body: CreateUserProfileWithUsernamePasswordCredentialsRequest,
    ) {
        body.validate()
        userProfileManager.createUserProfile(body)
    }
}
