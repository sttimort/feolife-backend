package dead.souls.feolife.service

import dead.souls.feolife.dao.DuplicateRoleNameException
import dead.souls.feolife.dao.RoleDao
import dead.souls.feolife.exception.FeolifeStatusConflictException
import dead.souls.feolife.logger
import dead.souls.feolife.model.Permission
import dead.souls.feolife.model.Role
import dead.souls.feolife.model.request.CreateRoleRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleService(
    private val roleDao: RoleDao,
) {
    fun getRoles(): List<Role> = roleDao.getRoles()

    fun getRolePermissions(roleName: String): List<Permission> = roleDao.getRolePermissions(roleName)

    @Transactional
    fun handleCreateRoleRequest(request: CreateRoleRequest) {
        val role = createRoleOrThrow(request)
        roleDao.assignPermissions(roleId = role.id, permissions = request.permissions)
        log.debug { "Created role $role with permissions ${request.permissions}" }
    }

    private fun createRoleOrThrow(request: CreateRoleRequest) = try {
        roleDao.createRole(
            name = request.name,
            isAssignedOnUserProfileCreation = request.isAssignedOnUserProfileCreation,
        )
    } catch (e: DuplicateRoleNameException) {
        throw FeolifeStatusConflictException("Role with name ${request.name} already exists")
    }

    companion object {
        private val log by logger()
    }
}
