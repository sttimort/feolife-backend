package dead.souls.feolife.service

import dead.souls.feolife.dao.RoleDao
import dead.souls.feolife.model.Role
import dead.souls.feolife.model.request.AssignRolesRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserProfileRolesManager(
    private val userProfileManager: UserProfileManager,
    private val roleDao: RoleDao,
) {
    fun getUserProfileRoles(userProfileUuid: UUID): List<Role> = userProfileManager
        .getUserProfileByUuidOrThrow(userProfileUuid)
        .let {
            roleDao.getRolesByUserProfileId(userProfileId = it.id)
        }

    fun handleAssignRolesRequest(userProfileUuid: UUID, assignRolesRequest: AssignRolesRequest) {
        userProfileManager
            .getUserProfileByUuidOrThrow(userProfileUuid)
            .also {
                roleDao.assignRoles(userProfileId = it.id, roleUuids = assignRolesRequest.roleUuids)
            }
    }
}
