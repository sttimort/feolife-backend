package dead.souls.feolife.model.request

import dead.souls.feolife.model.Permission

data class SetRolePermissionsRequest(
    val permissions: List<Permission>,
)
