package dead.souls.feolife.controller

import dead.souls.feolife.model.Role
import dead.souls.feolife.model.request.AssignRolesRequest
import dead.souls.feolife.model.response.GetUserProfileRolesResponse
import dead.souls.feolife.service.UserProfileRolesManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class UserProfileRolesController(
    private val userProfileRolesManager: UserProfileRolesManager,
) {
    @GetMapping("user-profiles/{userProfileUuid}/roles")
    fun getUserProfileRoles(
        @PathVariable("userProfileUuid") userProfileUuid: UUID
    ): GetUserProfileRolesResponse =
        userProfileRolesManager
            .getUserProfileRoles(userProfileUuid)
            .map { it.toGetUserProfileRolesResponseRole() }
            .let {
                GetUserProfileRolesResponse(roles = it)
            }

    @PutMapping("user-profiles/{userProfileUuid}/roles")
    fun assignRoles(
        @PathVariable("userProfileUuid") userProfileUuid: UUID,
        @RequestBody body: AssignRolesRequest,
    ) {
        userProfileRolesManager.handleAssignRolesRequest(userProfileUuid, body)
    }
}

private fun Role.toGetUserProfileRolesResponseRole() = GetUserProfileRolesResponse.Role(
    uuid = uuid,
    name = name,
)
