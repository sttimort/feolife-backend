package dead.souls.feolife.controller

import dead.souls.feolife.model.Role
import dead.souls.feolife.model.request.CreateRoleRequest
import dead.souls.feolife.model.request.SetRolePermissionsRequest
import dead.souls.feolife.model.response.GetRolePermissionsResponse
import dead.souls.feolife.model.response.GetRolesResponse
import dead.souls.feolife.service.RoleService
import dead.souls.feolife.validation.request.validate
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("roles")
class RoleController(
    private val roleService: RoleService,
) {
    @GetMapping
    fun getRoles(): GetRolesResponse = GetRolesResponse(
        roles = roleService.getRoles().map { it.toGetRolesResponseRole() },
    )

    @GetMapping("/{roleUuid}/permissions")
    fun getPermissions(@PathVariable("roleUuid") roleUuid: UUID): GetRolePermissionsResponse =
        GetRolePermissionsResponse(
            permissions = roleService.getRolePermissionsByRoleUuid(roleUuid)
        )

    @PostMapping
    fun createRole(@RequestBody body: CreateRoleRequest) {
        body.validate()
        roleService.handleCreateRoleRequest(body)
    }

    @PutMapping("/{roleUuid}/permissions")
    fun setRolePermissions(
        @PathVariable roleUuid: UUID,
        @RequestBody body: SetRolePermissionsRequest,
    ) {
        roleService.handleSetRolePermissionsRequest(roleUuid, body)
    }

    @DeleteMapping("/{roleUuid}")
    fun deleteRole(@PathVariable roleUuid: UUID) {
        roleService.deleteRoleByUuid(roleUuid)
    }
}

private fun Role.toGetRolesResponseRole() =
    GetRolesResponse.Role(
        uuid = uuid,
        name = name,
        isAssignedOnUserProfileCreation = isAssignedOnUserProfileCreation,
    )
