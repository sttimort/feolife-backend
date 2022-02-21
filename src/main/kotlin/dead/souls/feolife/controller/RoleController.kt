package dead.souls.feolife.controller

import dead.souls.feolife.model.Role
import dead.souls.feolife.model.request.CreateRoleRequest
import dead.souls.feolife.model.response.GetRolePermissionsResponse
import dead.souls.feolife.model.response.GetRolesResponse
import dead.souls.feolife.service.RoleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("roles")
class RoleController(
    private val roleService: RoleService,
) {
    @GetMapping
    fun getRoles(): GetRolesResponse = GetRolesResponse(
        roles = roleService.getRoles().map { it.toGetRolesResponseRole() },
    )

    @GetMapping("/{roleName}/permissions")
    fun getPermissions(@PathVariable("roleName") roleName: String): GetRolePermissionsResponse =
        GetRolePermissionsResponse(
            permissions = roleService.getRolePermissions(roleName)
        )

    @PostMapping
    fun createRole(@RequestBody body: CreateRoleRequest) {
        roleService.handleCreateRoleRequest(body)
    }
}

private fun Role.toGetRolesResponseRole() =
    GetRolesResponse.Role(name = name, isAssignedOnUserProfileCreation = isAssignedOnUserProfileCreation)
