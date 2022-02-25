package dead.souls.feolife.service

import dead.souls.feolife.dao.DuplicateRoleNameException
import dead.souls.feolife.dao.RoleDao
import dead.souls.feolife.exception.FeolifeStatusConflictException
import dead.souls.feolife.logger
import dead.souls.feolife.model.Permission
import dead.souls.feolife.model.Role
import dead.souls.feolife.model.request.AssignRolesRequest
import dead.souls.feolife.model.request.CreateRoleRequest
import dead.souls.feolife.model.request.SetRolePermissionsRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RoleService(
    private val userProfileManager: UserProfileManager,
    private val roleDao: RoleDao,
) {
    fun getRoles(): List<Role> = roleDao.getRoles()

    fun getRolePermissionsByRoleUuid(roleUuid: UUID): List<Permission> = roleDao.getRolePermissionsByRoleUuid(roleUuid)

    @Transactional
    fun handleCreateRoleRequest(request: CreateRoleRequest) {
        val role = createRoleOrThrow(request)
        roleDao.assignPermissions(roleId = role.id, permissions = request.permissions)
        log.debug { "Created role $role with permissions ${request.permissions}" }
    }

    fun handleSetRolePermissionsRequest(roleUuid: UUID, request: SetRolePermissionsRequest) {
        roleDao.setRolePermissionsByRoleUuid(roleUuid, request.permissions)
    }

    fun deleteRoleByUuid(roleUuid: UUID) {
        roleDao.deleteRoleByUuid(roleUuid)
    }

    private fun createRoleOrThrow(request: CreateRoleRequest) = try {
        roleDao.createRole(
            uuid = UUID.randomUUID(),
            name = request.name.trim(),
            isAssignedOnUserProfileCreation = request.isAssignedOnUserProfileCreation,
        )
    } catch (e: DuplicateRoleNameException) {
        throw FeolifeStatusConflictException("Role with name ${request.name} already exists")
    }

    companion object {
        private val log by logger()
    }
}
