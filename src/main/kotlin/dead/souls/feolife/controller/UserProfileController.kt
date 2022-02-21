package dead.souls.feolife.controller

import dead.souls.feolife.model.request.CreateUserProfileWithUsernamePasswordCredentialsRequest
import dead.souls.feolife.service.UserProfileManager
import dead.souls.feolife.validation.request.validate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserProfileController(
    private val userProfileManager: UserProfileManager,
) {
    @PostMapping("username-password-profiles")
    @ResponseStatus(HttpStatus.CREATED)
    fun createProfileWithUsernamePasswordCredentials(
        @RequestBody body: CreateUserProfileWithUsernamePasswordCredentialsRequest,
    ) {
        body.validate()
        userProfileManager.createUserProfile(body)
    }
}
