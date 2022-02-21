package dead.souls.feolife.controller

import dead.souls.feolife.model.FeolifePrincipal
import dead.souls.feolife.model.response.GetMyUserProfileResponse
import dead.souls.feolife.service.UserProfileManager
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("my-user-profile")
class MyUserProfileController(
    private val userProfileManager: UserProfileManager,
) {
    @GetMapping
    fun getMyUserProfile(
        @AuthenticationPrincipal principal: FeolifePrincipal,
    ): GetMyUserProfileResponse =
        userProfileManager.handleGetMyUserProfileRequest(userProfileUuid = principal.userProfileUuid)
}
